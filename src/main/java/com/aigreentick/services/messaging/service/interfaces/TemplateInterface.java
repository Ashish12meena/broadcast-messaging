package com.aigreentick.services.messaging.service.interfaces;

import org.springframework.data.domain.Page;

import com.aigreentick.services.messaging.dto.TemplateDto;

public interface TemplateInterface {
    Page<TemplateDto> getTemplatesByUser(String email, String search, Integer page, Integer pageSize);
    void   createTemplateForUser(TemplateDto templateDto, String email);
}

