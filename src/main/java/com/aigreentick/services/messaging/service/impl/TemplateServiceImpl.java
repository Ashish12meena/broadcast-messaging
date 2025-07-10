package com.aigreentick.services.messaging.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.impl.UserServiceImpl;
import com.aigreentick.services.messaging.dto.template.TemplateDto;
import com.aigreentick.services.messaging.dto.template.TemplateStatsDto;
import com.aigreentick.services.messaging.dto.template.TemplateUpdateRequest;
import com.aigreentick.services.messaging.enums.TemplateStatus;
import com.aigreentick.services.messaging.mapper.TemplateMapper;
import com.aigreentick.services.messaging.model.Template;
import com.aigreentick.services.messaging.model.TemplateComponent;
import com.aigreentick.services.messaging.repository.TemplateRepository;
import com.aigreentick.services.messaging.service.interfaces.TemplateInterface;

import jakarta.persistence.criteria.Predicate;

@Service
public class TemplateServiceImpl implements TemplateInterface {
    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;
    private final UserServiceImpl userService;

    @Value("${pagination.page-size}")
    private int defaultPageSize;

    public TemplateServiceImpl(TemplateRepository templateRepository, TemplateMapper templateMapper,
            UserServiceImpl userService) {
        this.templateRepository = templateRepository;
        this.templateMapper = templateMapper;
        this.userService = userService;
    }

    /**
     * Fetches paginated templates for the given user.
     */
    @Override
    public Page<TemplateDto> getTemplatesByUser(String email, String search, Integer page, Integer pageSize) {
        User user = userService.findByEmail(email);
        int sizeOfPage = (pageSize != null && pageSize > 0)
                ? pageSize
                : defaultPageSize;

        Pageable pageable = PageRequest.of(page, sizeOfPage, Sort.by(Sort.Direction.DESC, "id"));
        Page<Template> templatePage;
        if (search == null || search.isBlank()) {
            templatePage = templateRepository.findByUser(user, pageable);
        } else {
            templatePage = templateRepository.findByUserAndNameContainingIgnoreCase(user, search, pageable);
        }
        return templatePage.map(templateMapper::toTemplateDto);
    }

    /**
     * Create Template and save currently not approved by facebook.
     */
    @Override
    @Transactional
    public void createTemplateForUser(TemplateDto templateDto, String email) {
        User user = userService.findByEmail(email);
        Template template = templateMapper.toTemplateEntity(templateDto, user);
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
        if (request.getWaId() != null)
            template.setWaId(request.getWaId());
        if (request.getPayload() != null)
            template.setPayload(request.getPayload());
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

    public boolean deleteTemplateByIdAndUser(Long templateId, String username) {
        Optional<Template> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isEmpty())
            return false;

        Template template = optionalTemplate.get();

        if (template.getUser() == null || !template.getUser().getUsername().equals(username)) {
            return false;
        }

        templateRepository.delete(template);
        return true;
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
}
