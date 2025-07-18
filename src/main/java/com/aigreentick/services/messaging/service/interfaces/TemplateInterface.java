package com.aigreentick.services.messaging.service.interfaces;

import org.springframework.data.domain.Page;

import com.aigreentick.services.messaging.dto.template.TemplateRequestDto;
import com.aigreentick.services.messaging.dto.template.TemplateResponseDto;

public interface TemplateInterface {
    Page<TemplateResponseDto> getTemplatesByUser(String email, String search, Integer page, Integer pageSize);
    void   createTemplateForUser(TemplateRequestDto templateDto, String email);
}

