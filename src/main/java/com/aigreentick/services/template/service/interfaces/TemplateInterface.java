package com.aigreentick.services.template.service.interfaces;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import com.aigreentick.services.template.dto.CreateTemplateResponseDto;
import com.aigreentick.services.template.dto.FacebookApiCredentialsDto;
import com.aigreentick.services.template.dto.TemplateDto;
import com.aigreentick.services.template.dto.TemplateRequestDto;
import com.aigreentick.services.template.dto.TemplateResponseDto;
import com.aigreentick.services.template.dto.TemplateStatsDto;
import com.aigreentick.services.template.dto.TemplateUpdateRequest;
import com.aigreentick.services.template.enums.TemplateStatus;
import com.aigreentick.services.template.model.Template;

public interface TemplateInterface {
    CreateTemplateResponseDto createTemplateForUser(TemplateRequestDto templateDto, String email);

    Page<TemplateResponseDto> getTemplatesByUser(String email, String status, String search, Integer page,
            Integer pageSize);

    void updateTemplateStatus(Long id, TemplateStatus status, String reason);

    void updateTemplate(Long templateId, TemplateUpdateRequest request, String username);

    boolean SoftDeleteTemplateByIdAndUser(Long templateId, String username);

    TemplateDto deleteUserTemplateById(String username, Long templateId,
            FacebookApiCredentialsDto accessCredentials);

    Page<Template> getAdminFilteredTemplates(
            Long userId,
            String status,
            String name,
            String waId,
            String category,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size);

    TemplateStatsDto getTemplateStats();

    boolean adminDeleteTemplate(Long templateId);

    Template findById(Long templateId);

}
