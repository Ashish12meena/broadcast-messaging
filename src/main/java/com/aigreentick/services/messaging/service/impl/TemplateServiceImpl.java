package com.aigreentick.services.messaging.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.impl.UserServiceImpl;
import com.aigreentick.services.messaging.dto.TemplateDto;
import com.aigreentick.services.messaging.mapper.TemplateMapper;
import com.aigreentick.services.messaging.model.Template;
import com.aigreentick.services.messaging.repository.TemplateRepository;
import com.aigreentick.services.messaging.service.interfaces.TemplateInterface;

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

}
