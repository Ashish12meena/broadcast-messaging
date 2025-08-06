package com.aigreentick.services.template.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.messaging.exception.ResponseStatusException;
import com.aigreentick.services.template.constants.TemplateConstants;
import com.aigreentick.services.template.dto.TemplateCreateResponseDto;
import com.aigreentick.services.template.dto.TemplateResponseDto;
import com.aigreentick.services.template.dto.TemplateStatsDto;
import com.aigreentick.services.template.dto.TemplateUpdateRequest;
import com.aigreentick.services.template.dto.buildTemplate.BaseTemplateRequestDto;
import com.aigreentick.services.template.dto.buildTemplate.TemplateCreateRequestDto;
import com.aigreentick.services.template.dto.buildTemplate.TemplateRequestDto;
import com.aigreentick.services.template.enums.TemplateStatus;
import com.aigreentick.services.template.exception.TemplateAlreadyExistsException;
import com.aigreentick.services.template.exception.TemplateNotFoundException;
import com.aigreentick.services.template.mapper.TemplateMapper;
import com.aigreentick.services.template.model.Template;
import com.aigreentick.services.template.repository.TemplateRepository;
import com.aigreentick.services.template.service.interfaces.TemplateInterface;
import com.aigreentick.services.whatsapp.dto.template.FacebookApiResponse;
import com.aigreentick.services.whatsapp.service.impl.WhatsAppTemplateServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateInterface {
    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;
    private final UserService userService;
    private final BuildTemplateServiceImpl buildTemplateService;
    private final WhatsAppTemplateServiceImpl whatsAppTemplateService;
    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${pagination.page-size}")
    private int defaultPageSize;

    /**
     * Create Template and save currently not approved by facebook.
     */
    @Transactional
    public TemplateCreateResponseDto createTemplate(TemplateCreateRequestDto request, Long userId) {
        // Get user
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found with id " + userId));

        BaseTemplateRequestDto baseTemplateRequestDto = request.getTemplate();

        // Check for duplicate template template
        checkDuplicateTemplate(baseTemplateRequestDto.getName());

        // Convert ObjectNode to JSON String
        String jsonRequest = serializeTemplate(baseTemplateRequestDto);

        // Call WhatsAppTemplateService
        FacebookApiResponse<JsonNode> fbResponse = whatsAppTemplateService.sendTemplateToFacebook(
                jsonRequest, request.getWabaId(), request.getAccessToken(), request.getApiVersion());

        // Handle server-side errors (isSuccess = false)
        if (!fbResponse.isSuccess()) {
            return new TemplateCreateResponseDto(false, null, null, null,
                    fbResponse.getErrorMessage(), null);
        }

        JsonNode data = fbResponse.getData();

        if (data.has("error")) {
            // Facebook error payload
            String errorMessage = data.path("error").path("message").asText("Unknown error");
            return new TemplateCreateResponseDto(false, null, null, null, errorMessage, data);
        }

        String templateId = data.path("id").asText(null);
        String status = data.path("status").asText(null);
        String category = data.path("category").asText(null);

        if (templateId == null || status == null) {
            return new TemplateCreateResponseDto(false, null, null, null,
                    "Invalid response from Facebook API", data);
        }

        JsonNode componentsJson = objectMapper.valueToTree(baseTemplateRequestDto.getComponents());

        Template template = templateMapper.toTemplateEntity(user, jsonRequest, baseTemplateRequestDto, componentsJson,
                data);
        template.setMetaTemplateId(templateId);
        template.setStatus(TemplateStatus.valueOf(status.toUpperCase()));
        template.setCategory(category);
        template.setResponse(data);
        templateRepository.save(template);

        return new TemplateCreateResponseDto(true, templateId, status, category, null, null);

    }

    /**
     * Fetches paginated templates for the given user.
     */
    // @Override
    // public Page<TemplateResponseDto> getTemplatesByUser(Long userId, String status, String search, Integer page,
    //         Integer pageSize) {
    //     User user = userService.findById(userId)
    //             .orElseThrow(() -> new UserNotFoundException("User Not Found with id " + userId));
    //     int sizeOfPage = (pageSize != null && pageSize > 0)
    //             ? pageSize
    //             : defaultPageSize;

    //     Pageable pageable = PageRequest.of(page, sizeOfPage, Sort.by(Sort.Direction.DESC, "id"));
    //     Page<Template> templatePage;
    //     TemplateStatus statusEnum = null;
    //     if (status != null && !status.isBlank()) {
    //         try {
    //             statusEnum = TemplateStatus.valueOf(status.toUpperCase());
    //         } catch (IllegalArgumentException e) {
    //             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid template status: " + status);
    //         }
    //     }
    //     if (statusEnum != null && search != null && !search.isBlank()) {
    //         templatePage = templateRepository.findByUserAndStatusAndNameContainingIgnoreCase(user, statusEnum, search,
    //                 pageable);
    //     } else if (statusEnum != null) {
    //         templatePage = templateRepository.findByUserAndStatus(user, statusEnum, pageable);
    //     } else if (search != null && !search.isBlank()) {
    //         templatePage = templateRepository.findByUserAndNameContainingIgnoreCase(user, search, pageable);
    //     } else {
    //         templatePage = templateRepository.findByUser(user, pageable);
    //     }
    //     return templatePage.map(templateMapper::toTemplateDto);
    // }

    @Transactional
    public void updateTemplateStatus(Long id, String status, String reason) {
        TemplateStatus enumStatus = TemplateStatus.valueOf(status.toUpperCase());

        entityManager.createQuery("""
                    UPDATE Template t
                    SET t.status = :status,
                        t.rejectionReason = :reason,
                        t.updatedAt = :updatedAt
                    WHERE t.id = :id
                """)
                .setParameter("status", enumStatus)
                .setParameter("reason", reason)
                .setParameter("updatedAt", LocalDateTime.now())
                .setParameter("id", id)
                .executeUpdate();
    }

    public void updateTemplate(String templateId, TemplateUpdateRequest request, String username) {
      
    }

    public boolean SoftDeleteTemplateByIdAndUser(String templateId, String username) {
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isEmpty())
            return false;

        Template template = optionalTemplate.get();


        // Soft delete by setting deletedAt
        template.setDeletedAt(LocalDateTime.now());
        templateRepository.save(template);
        return true;
    }

    

    // public Page<Template> getAdminFilteredTemplates(
    //         Long userId,
    //         String status,
    //         String name,
    //         String waId,
    //         String category,
    //         LocalDateTime fromDate,
    //         LocalDateTime toDate,
    //         int page,
    //         int size) {
    //     Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    //     Specification<Template> spec = (root, query, cb) -> {
    //         List<Predicate> predicates = new ArrayList<>();

    //         if (userId != null) {
    //             predicates.add(cb.equal(root.get("user").get("id"), userId));
    //         }

    //         if (status != null && !status.isBlank()) {
    //             predicates.add(cb.equal(root.get("status"), TemplateStatus.valueOf(status)));
    //         }

    //         if (name != null && !name.isBlank()) {
    //             predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    //         }

    //         if (waId != null && !waId.isBlank()) {
    //             predicates.add(cb.equal(root.get("waId"), waId));
    //         }

    //         if (category != null && !category.isBlank()) {
    //             predicates.add(cb.equal(root.get("category"), category));
    //         }

    //         if (fromDate != null && toDate != null) {
    //             predicates.add(cb.between(root.get("createdAt"), fromDate, toDate));
    //         } else if (fromDate != null) {
    //             predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
    //         } else if (toDate != null) {
    //             predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
    //         }

    //         // Soft delete check
    //         predicates.add(cb.isNull(root.get("deletedAt")));

    //         return cb.and(predicates.toArray(new Predicate[0]));
    //     };

    //     return templateRepository.findAll(spec, pageable);
    // }

    //
    // public TemplateStatsDto getTemplateStats() {
    //     long total = templateRepository.countByDeletedAtIsNull();

    //     // Count by status
    //     List<Object[]> statusData = templateRepository.countByStatusGrouped();
    //     Map<String, Long> statusCounts = statusData.stream()
    //             .collect(Collectors.toMap(
    //                     row -> ((TemplateStatus) row[0]).name(),
    //                     row -> (Long) row[1]));

    //     // Count by category
    //     List<Object[]> categoryData = templateRepository.countByCategoryGrouped();
    //     Map<String, Long> categoryCounts = categoryData.stream()
    //             .collect(Collectors.toMap(
    //                     row -> (String) row[0],
    //                     row -> (Long) row[1]));

    //     return new TemplateStatsDto(total, statusCounts, categoryCounts);
    // }

    public boolean adminDeleteTemplate(String templateId) {
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

    public Template findById(String templateId) {
        if (templateId == null) {
            throw new IllegalArgumentException("Template ID must not be null");
        }
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found"));
    }

    public boolean existsById(String templateId) {
        if (templateId != null) {
            return templateRepository.existsById(templateId);
        } else {
            throw new IllegalArgumentException("Template ID must not be null");
        }
    }

    public ObjectNode buildRequestBodyForApproval(TemplateRequestDto dto) {
        return buildTemplateService.buildRequestBodyForApproval(dto);
    }

    public ObjectNode buildTemplateForSending(Template template, Map<String, String> parameters, String phoneNumber) {
        return buildTemplateService.buildTemplateForSending(template, parameters, phoneNumber);
    }

    // Private methods ***************************
    private void checkDuplicateTemplate(String name) {
        if (templateRepository.findByName(name).isPresent()) {
            throw new TemplateAlreadyExistsException(String.format(TemplateConstants.TEMPLATE_EXISTS_MSG, name));
        }
    }

    private String serializeTemplate(BaseTemplateRequestDto baseDto) {
        try {
            return objectMapper.writeValueAsString(baseDto);
        } catch (Exception e) {
            log.error("Failed to serialize template request to JSON", e);
            throw new IllegalStateException("Template serialization failed");
        }
    }

}
