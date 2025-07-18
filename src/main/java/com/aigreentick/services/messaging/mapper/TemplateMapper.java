package com.aigreentick.services.messaging.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.dto.template.FacebookTemplateResponse;
import com.aigreentick.services.messaging.dto.template.TemplateComponentResponseDto;
import com.aigreentick.services.messaging.dto.template.TemplateRequestDto;
import com.aigreentick.services.messaging.dto.template.TemplateResponseDto;
import com.aigreentick.services.messaging.enums.TemplateStatus;
import com.aigreentick.services.messaging.model.template.Template;
import com.aigreentick.services.messaging.model.template.TemplateComponent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TemplateMapper {
    private final TemplateComponentMapper componentMapper;

    @Autowired
    private ObjectMapper objectMapper;

    public TemplateMapper(TemplateComponentMapper componentMapper) {
        this.componentMapper = componentMapper;
    }

    public TemplateResponseDto toTemplateDto(Template template) {
        TemplateResponseDto templateDto = new TemplateResponseDto();
        templateDto.setId(template.getId());
        templateDto.setName(template.getName());
        templateDto.setStatus(template.getStatus().name()); // Enum to String
        templateDto.setCategory(template.getCategory());
        templateDto.setWhatsappId(template.getWhatsappId());
        templateDto.setPayload(template.getSubmissionPayload());
        templateDto.setLanguage(template.getLanguage());
        templateDto.setMetaTemplateId(template.getMetaTemplateId());

        // Lazy load user email if user is not null
        if (template.getUser() != null) {
            templateDto.setEmail(template.getUser().getUsername());
        }
        List<TemplateComponentResponseDto> components = template.getComponents().stream()
                .map(componentMapper::toTemplateComponentDto)
                .collect(Collectors.toList());

        templateDto.setComponents(components);
        return templateDto;
    }

    public List<TemplateResponseDto> toTemplateDtoList(List<Template> templates) {
        return templates.stream().map(this::toTemplateDto).toList();
    }

    public Template toTemplateEntity(TemplateResponseDto dto, User user) {
        Template template = new Template();
        template.setUser(user); // set from context
        template.setName(dto.getName());
        template.setCategory(dto.getCategory());
        template.setWhatsappId(dto.getWhatsappId());
        template.setLanguage(dto.getLanguage());
        template.setStatus(TemplateStatus.PENDING);

        // Convert component DTOs to entities
        List<TemplateComponent> components = dto.getComponents().stream()
                .map(componentDto -> componentMapper.toTemplateComponentEntity(componentDto, template)) // pass parent
                                                                                                        // reference
                .collect(Collectors.toList());

        template.setComponents(components);
        return template;
    }

    public Template toTemplateEntity(TemplateRequestDto dto, User user , FacebookTemplateResponse response) {
        Template template = new Template();
        template.setUser(user);
        template.setName(dto.getName());
        template.setWhatsappId(dto.getWabaId());
        template.setLanguage(dto.getLanguage());
        template.setStatus(TemplateStatus.valueOf(response.getStatus().toUpperCase())); // Convert status to enum
        template.setMetaTemplateId(response.getId());
        template.setCategory(response.getCategory());

        // 💡 Set components_json as actual JSON
        JsonNode componentsJson = objectMapper.valueToTree(dto.getComponents());
        template.setComponentsJson(componentsJson);

        // Entity mapping
        List<TemplateComponent> components = Optional.ofNullable(dto.getComponents())
                .orElse(Collections.emptyList())
                .stream()
                .map(componentDto -> componentMapper.toTemplateComponentEntity(componentDto, template))
                .collect(Collectors.toList());

        template.setComponents(components);

        return template;
    }

}
