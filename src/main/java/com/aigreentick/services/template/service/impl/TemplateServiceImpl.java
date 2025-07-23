package com.aigreentick.services.template.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.messaging.exception.ResponseStatusException;
import com.aigreentick.services.messaging.exception.UnauthorizedTemplateAccessException;
import com.aigreentick.services.template.dto.CreateTemplateResponseDto;
import com.aigreentick.services.template.dto.FacebookApiCredentialsDto;
import com.aigreentick.services.template.dto.TemplateComponentButtonRequestDto;
import com.aigreentick.services.template.dto.TemplateComponentRequestDto;
import com.aigreentick.services.template.dto.TemplateDto;
import com.aigreentick.services.template.dto.TemplateRequestDto;
import com.aigreentick.services.template.dto.TemplateResponseDto;
import com.aigreentick.services.template.dto.TemplateStatsDto;
import com.aigreentick.services.template.dto.TemplateUpdateRequest;
import com.aigreentick.services.template.enums.TemplateStatus;
import com.aigreentick.services.template.exception.TemplateAlreadyExistsException;
import com.aigreentick.services.template.exception.TemplateNotFoundException;
import com.aigreentick.services.template.mapper.TemplateMapper;
import com.aigreentick.services.template.model.Template;
import com.aigreentick.services.template.model.TemplateComponent;
import com.aigreentick.services.template.repository.TemplateRepository;
import com.aigreentick.services.template.service.interfaces.TemplateInterface;
import com.aigreentick.services.whatsapp.service.impl.WhatsappServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateInterface {
    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;
    private final UserService userService;
    private final WhatsappServiceImpl whatsappService;

    @Value("${pagination.page-size}")
    private int defaultPageSize;

    /**
     * Create Template and save currently not approved by facebook.
     */
    public CreateTemplateResponseDto createTemplateForUser(TemplateRequestDto templateDto, String email) {
        // Check for duplicate template name
        if (templateRepository.findByName(templateDto.getName()).isPresent()) {
            throw new TemplateAlreadyExistsException("Template with name " + templateDto.getName() + " already exist");
        }

        // Build the request body for Facebook approval
        ObjectNode requestBody = buildRequestBodyForApproval(templateDto);

        // Convert ObjectNode to JSON String
        String jsonRequest;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonRequest = mapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize template request to JSON.", e);
        }

        // Submit template to Facebook
        Map<String, Object> rawResponse = whatsappService.submitTemplateToFacebookForApproval(jsonRequest);

        // Get user and save the template
        User user = userService.findByEmail(email);
        Template template = templateMapper.toTemplateEntity(templateDto, user, rawResponse);
        template.setSubmissionPayload(jsonRequest);
        templateRepository.save(template);

        return CreateTemplateResponseDto.builder()
                .id(template.getId())
                .name(template.getName())
                .status(template.getStatus().name())
                .metaTemplateId(template.getMetaTemplateId())
                .category(template.getCategory())
                .language(template.getLanguage())
                .build();

    }

    /**
     * Fetches paginated templates for the given user.
     */
    @Override
    public Page<TemplateResponseDto> getTemplatesByUser(String email, String status, String search, Integer page,
            Integer pageSize) {
        User user = userService.findByEmail(email);
        int sizeOfPage = (pageSize != null && pageSize > 0)
                ? pageSize
                : defaultPageSize;

        Pageable pageable = PageRequest.of(page, sizeOfPage, Sort.by(Sort.Direction.DESC, "id"));
        Page<Template> templatePage;
        TemplateStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = TemplateStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid template status: " + status);
            }
        }
        if (statusEnum != null && search != null && !search.isBlank()) {
            templatePage = templateRepository.findByUserAndStatusAndNameContainingIgnoreCase(user, statusEnum, search,
                    pageable);
        } else if (statusEnum != null) {
            templatePage = templateRepository.findByUserAndStatus(user, statusEnum, pageable);
        } else if (search != null && !search.isBlank()) {
            templatePage = templateRepository.findByUserAndNameContainingIgnoreCase(user, search, pageable);
        } else {
            templatePage = templateRepository.findByUser(user, pageable);
        }
        return templatePage.map(templateMapper::toTemplateDto);
    }

    /**
     * Builds the request body JSON for WhatsApp template approval.
     * Delegates component-specific logic to helper methods for clarity.
     */
    private ObjectNode buildRequestBodyForApproval(TemplateRequestDto dto) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        // Basic required fields for WhatsApp template
        root.put("name", dto.getName());
        root.put("language", dto.getLanguage());
        root.put("category", dto.getCategory());

        ArrayNode componentsArray = mapper.createArrayNode();

        // Handle each component based on type
        for (TemplateComponentRequestDto componentDto : dto.getComponents()) {
            String type = componentDto.getType().toUpperCase();

            ObjectNode componentNode = switch (type) {
                case "HEADER" -> buildHeaderComponent(componentDto, mapper);
                case "BODY" -> buildBodyComponent(componentDto, mapper);
                case "FOOTER" -> buildFooterComponent(componentDto, mapper);
                case "BUTTONS" -> buildButtonsComponent(componentDto, mapper);
                default -> throw new IllegalArgumentException("Unsupported component type: " + type);
            };

            componentsArray.add(componentNode);
        }

        root.set("components", componentsArray);
        return root;
    }

    /**
     * Builds HEADER component with TEXT or media (IMAGE/VIDEO/DOCUMENT).
     */
    private ObjectNode buildHeaderComponent(TemplateComponentRequestDto dto, ObjectMapper mapper) {
        ObjectNode headerNode = mapper.createObjectNode();
        headerNode.put("type", "HEADER");
        headerNode.put("format", dto.getFormat().toUpperCase());

        ObjectNode example = mapper.createObjectNode();

        if ("TEXT".equalsIgnoreCase(dto.getFormat())) {
            headerNode.put("text", dto.getText());
        } else {
            // For media header, URL must be provided
            if (dto.getMediaUrl() == null || dto.getMediaUrl().isBlank()) {
                throw new IllegalArgumentException("Media URL is required for HEADER with format: " + dto.getFormat());
            }

            ArrayNode mediaExample = mapper.createArrayNode();
            mediaExample.add(dto.getMediaUrl());

            String key = switch (dto.getFormat().toUpperCase()) {
                case "IMAGE" -> "header_image";
                case "VIDEO" -> "header_video";
                case "DOCUMENT" -> "header_document";
                default -> throw new IllegalArgumentException("Unsupported HEADER format: " + dto.getFormat());
            };

            example.set(key, mediaExample);
        }

        headerNode.set("example", example);
        return headerNode;
    }

    /**
     * Builds BODY component with optional placeholder detection.
     */
    private ObjectNode buildBodyComponent(TemplateComponentRequestDto dto, ObjectMapper mapper) {
        ObjectNode bodyNode = mapper.createObjectNode();
        bodyNode.put("type", "BODY");
        bodyNode.put("text", dto.getText());

        // Only add example node if placeholders exist
        if (dto.getText() != null && dto.getText().contains("{{")) {
            ObjectNode example = mapper.createObjectNode();
            ArrayNode bodyExamples = mapper.createArrayNode();
            ArrayNode row = mapper.createArrayNode(); // empty row (no real example values)
            bodyExamples.add(row);
            example.set("body_text", bodyExamples);
            bodyNode.set("example", example);
        }

        return bodyNode;
    }

    /**
     * Builds FOOTER component containing static text.
     */
    private ObjectNode buildFooterComponent(TemplateComponentRequestDto dto, ObjectMapper mapper) {
        ObjectNode footerNode = mapper.createObjectNode();
        footerNode.put("type", "FOOTER");
        footerNode.put("text", dto.getText());
        return footerNode;
    }

    /**
     * Builds BUTTONS component for URL or PHONE_NUMBER types.
     */
    private ObjectNode buildButtonsComponent(TemplateComponentRequestDto dto, ObjectMapper mapper) {
        if (dto.getButtons() == null || dto.getButtons().isEmpty()) {
            throw new IllegalArgumentException("Buttons required for BUTTONS component.");
        }

        ObjectNode buttonNode = mapper.createObjectNode();
        buttonNode.put("type", "BUTTONS");

        ArrayNode buttonsArray = mapper.createArrayNode();

        for (TemplateComponentButtonRequestDto btn : dto.getButtons()) {
            ObjectNode btnNode = mapper.createObjectNode();
            btnNode.put("type", btn.getType().toUpperCase());

            ObjectNode buttonPayload = mapper.createObjectNode();
            buttonPayload.put("type", btn.getType().toUpperCase());
            buttonPayload.put("text", btn.getText());

            if ("URL".equalsIgnoreCase(btn.getType())) {
                buttonPayload.put("url", btn.getUrl());
            } else if ("PHONE_NUMBER".equalsIgnoreCase(btn.getType())) {
                buttonPayload.put("phone_number", btn.getPhoneNumber());
            }

            btnNode.set("button", buttonPayload);
            buttonsArray.add(btnNode);
        }

        buttonNode.set("buttons", buttonsArray);
        return buttonNode;
    }

    public void updateTemplateStatus(Long id, TemplateStatus status, String reason) {
        Template template = templateRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        template.setStatus(status);
        template.setRejectionReason(reason);
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.save(template);
    }

    public void updateTemplate(Long templateId, TemplateUpdateRequest request, String username) {
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isEmpty())
            return;

        Template template = optionalTemplate.get();

        // ✅ Ensure current user owns this template
        if (template.getUser() == null || !template.getUser().getUsername().equals(username)) {
            return; // Or optionally throw AccessDeniedException
        }

        // ✅ Perform updates
        if (request.getName() != null)
            template.setName(request.getName());
        if (request.getLanguage() != null)
            template.setLanguage(request.getLanguage());
        if (request.getCategory() != null)
            template.setCategory(request.getCategory());
        if (request.getPreviousCategory() != null)
            template.setPreviousCategory(request.getPreviousCategory());
        if (request.getWhatsappId() != null)
            template.setWhatsappId(request.getWhatsappId());
        if (request.getPayload() != null)
            template.setSubmissionPayload(request.getPayload());
        if (request.getResponse() != null)
            template.setResponse(request.getResponse());

        // ✅ Update components (replace all)
        if (request.getComponents() != null) {
            template.getComponents().clear();
            for (TemplateComponent component : request.getComponents()) {
                component.setTemplate(template); // maintain bidirectional relationship
            }
            template.getComponents().addAll(request.getComponents());
        }

        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.save(template);
    }

    public boolean SoftDeleteTemplateByIdAndUser(Long templateId, String username) {
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isEmpty())
            return false;

        Template template = optionalTemplate.get();

        // Ensure the logged-in user owns this template
        if (template.getUser() == null || !template.getUser().getUsername().equals(username)) {
            return false;
        }

        // Soft delete by setting deletedAt
        template.setDeletedAt(LocalDateTime.now());
        templateRepository.save(template);
        return true;
    }

    public TemplateDto deleteUserTemplateById(String username, Long templateId,
            FacebookApiCredentialsDto accessCredentials) {
        // 1. Validate template existence
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found"));

        // 2. Check ownership
        if (template.getUser() == null || !template.getUser().getUsername().equals(username)) {
            throw new UnauthorizedTemplateAccessException(
                    "You do not have permission to delete this template");
        }

        // 3. Call Facebook API to delete the template
        boolean success = whatsappService.deleteTemplateFromFacebook(
                template.getWhatsappId(),
                template.getName(),
                accessCredentials.getAccessToken(),
                accessCredentials.getApiVersion());

        // 4. Check if Facebook API call was successful
        if (!success) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to delete template from Facebook");
        }

        // 5. Delete from local DB
        templateRepository.delete(template);

        return new TemplateDto(template);
    }

    public Page<Template> getAdminFilteredTemplates(
            Long userId,
            String status,
            String name,
            String waId,
            String category,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Template> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), TemplateStatus.valueOf(status)));
            }

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (waId != null && !waId.isBlank()) {
                predicates.add(cb.equal(root.get("waId"), waId));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (fromDate != null && toDate != null) {
                predicates.add(cb.between(root.get("createdAt"), fromDate, toDate));
            } else if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            } else if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }

            // Soft delete check
            predicates.add(cb.isNull(root.get("deletedAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return templateRepository.findAll(spec, pageable);
    }

    //
    public TemplateStatsDto getTemplateStats() {
        long total = templateRepository.countByDeletedAtIsNull();

        // Count by status
        List<Object[]> statusData = templateRepository.countByStatusGrouped();
        Map<String, Long> statusCounts = statusData.stream()
                .collect(Collectors.toMap(
                        row -> ((TemplateStatus) row[0]).name(),
                        row -> (Long) row[1]));

        // Count by category
        List<Object[]> categoryData = templateRepository.countByCategoryGrouped();
        Map<String, Long> categoryCounts = categoryData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]));

        return new TemplateStatsDto(total, statusCounts, categoryCounts);
    }

    public boolean adminDeleteTemplate(Long templateId) {
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isEmpty())
            return false;

        Template template = optionalTemplate.get();
        if (template.getDeletedAt() != null)
            return false; // already deleted

        template.setDeletedAt(LocalDateTime.now());
        templateRepository.save(template);
        return true;
    }

    public Template findById(Long templateId) {
        if (templateId == null) {
            throw new IllegalArgumentException("Template ID must not be null");
        }
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found"));
    }

    public boolean existsById(Long templateId) {
       if (templateId!=null) {
         return templateRepository.existsById(templateId);
       }else{
          throw new IllegalArgumentException("Template ID must not be null");
       }
    }
}
