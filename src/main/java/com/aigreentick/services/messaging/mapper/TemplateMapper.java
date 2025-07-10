package com.aigreentick.services.messaging.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.dto.template.TemplateComponentDto;
import com.aigreentick.services.messaging.dto.template.TemplateDto;
import com.aigreentick.services.messaging.enums.TemplateStatus;
import com.aigreentick.services.messaging.model.Template;
import com.aigreentick.services.messaging.model.TemplateComponent;

@Component
public class TemplateMapper {
    private final TemplateComponentMapper componentMapper;

    public TemplateMapper(TemplateComponentMapper componentMapper) {
        this.componentMapper = componentMapper;
    }

    public TemplateDto toTemplateDto(Template template) {
        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(template.getId());
        templateDto.setUserId(template.getUser() != null ? template.getUser().getId() : null); // Handle null user
        templateDto.setName(template.getName());
        templateDto.setStatus(template.getStatus().name()); // Enum to String
        templateDto.setCategory(template.getCategory());
        templateDto.setWaId(template.getWaId());
        templateDto.setPayload(template.getPayload());

        // Lazy load user email if user is not null
        if (template.getUser() != null) {
            templateDto.setEmail(template.getUser().getUsername());
        }
        List<TemplateComponentDto> components = template.getComponents().stream()
                .map(componentMapper::toTemplateComponentDto)
                .collect(Collectors.toList());

        templateDto.setComponents(components);
        return templateDto;
    }

    public List<TemplateDto> toTemplateDtoList(List<Template> templates) {
        return templates.stream().map(this::toTemplateDto).toList();
    }

    public Template toTemplateEntity(TemplateDto dto, User user) {
        Template template = new Template();
        template.setId(dto.getId()); // Optional: mostly for update
        template.setUser(user); // set from context
        template.setName(dto.getName());
        template.setCategory(dto.getCategory());
        template.setWaId(dto.getWaId());
        template.setPayload(dto.getPayload());
        template.setLanguage(dto.getLanguage());
        template.setStatus(TemplateStatus.pending);

        // Convert component DTOs to entities
        List<TemplateComponent> components = dto.getComponents().stream()
                .map(componentDto -> componentMapper.toTemplateComponentEntity(componentDto, template)) // pass parent reference
                .collect(Collectors.toList());

        template.setComponents(components);
        return template;
    }
}
