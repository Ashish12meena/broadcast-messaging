package com.aigreentick.services.template.service.interfaces;

import org.springframework.data.domain.Page;

import com.aigreentick.services.template.dto.TemplateRequestDto;
import com.aigreentick.services.template.dto.TemplateResponseDto;

public interface TemplateInterface {
    Page<TemplateResponseDto> getTemplatesByUser(String email, String search, Integer page, Integer pageSize);
    void   createTemplateForUser(TemplateRequestDto templateDto, String email);
}

