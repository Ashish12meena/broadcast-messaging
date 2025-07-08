package com.aigreentick.services.messaging.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.TemplateComponentButtonDto;
import com.aigreentick.services.messaging.dto.TemplateComponentDto;
import com.aigreentick.services.messaging.model.Template;
import com.aigreentick.services.messaging.model.TemplateComponent;
import com.aigreentick.services.messaging.model.TemplateComponentButton;

@Component
public class TemplateComponentMapper {

    private final TemplateComponentButtonMapper buttonMapper;

    public TemplateComponentMapper(TemplateComponentButtonMapper buttonMapper) {
        this.buttonMapper = buttonMapper;
    }

    public TemplateComponentDto toTemplateComponentDto(TemplateComponent component) {
        TemplateComponentDto dto = new TemplateComponentDto();
        dto.setId(component.getId());
        dto.setTemplateId(component.getTemplate().getId());
        dto.setType(component.getType());
        dto.setFormat(component.getFormat());
        dto.setText(component.getText());
        dto.setImageUrl(component.getImageUrl());

        List<TemplateComponentButtonDto> buttonDtos = component.getComponentsButtons().stream()
                .map(buttonMapper::toTemplateComponentButtonDto)
                .collect(Collectors.toList());

        dto.setButtons(buttonDtos);
        return dto;
    }

    public TemplateComponent toTemplateComponentEntity(TemplateComponentDto dto, Template template) {
        TemplateComponent component = new TemplateComponent();
        component.setId(dto.getId());
        component.setTemplate(template);
        component.setType(dto.getType());
        component.setFormat(dto.getFormat());
        component.setText(dto.getText());
        component.setImageUrl(dto.getImageUrl());
        List<TemplateComponentButton> buttons = dto.getButtons().stream()
                .map(buttonDto -> buttonMapper.toTemplateComponentButtonEntity(buttonDto, component,template)) // pass references
                .collect(Collectors.toList());

        component.setComponentsButtons(buttons);
        return component;

    }
}
