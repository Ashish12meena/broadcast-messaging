package com.aigreentick.services.template.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aigreentick.services.template.dto.TemplateComponentButtonResponseDto;
import com.aigreentick.services.template.dto.TemplateComponentRequestDto;
import com.aigreentick.services.template.dto.TemplateComponentResponseDto;
import com.aigreentick.services.template.model.Template;
import com.aigreentick.services.template.model.TemplateComponent;
import com.aigreentick.services.template.model.TemplateComponentButton;

@Component
public class TemplateComponentMapper {

    private final TemplateComponentButtonMapper buttonMapper;

    public TemplateComponentMapper(TemplateComponentButtonMapper buttonMapper) {
        this.buttonMapper = buttonMapper;
    }

    public TemplateComponentResponseDto toTemplateComponentDto(TemplateComponent component) {
        TemplateComponentResponseDto dto = new TemplateComponentResponseDto();
        dto.setId(component.getId());
        dto.setTemplateId(component.getTemplate().getId());
        dto.setType(component.getType());
        dto.setFormat(component.getFormat());
        dto.setText(component.getText());
        dto.setImageUrl(component.getImageUrl());

        List<TemplateComponentButtonResponseDto> buttonDtos = component.getComponentsButtons().stream()
                .map(buttonMapper::toTemplateComponentButtonDto)
                .collect(Collectors.toList());

        dto.setButtons(buttonDtos);
        return dto;
    }

    public TemplateComponent toTemplateComponentEntity(TemplateComponentResponseDto dto, Template template) {
        TemplateComponent component = new TemplateComponent();
        component.setTemplate(template);
        component.setType(dto.getType());
        component.setFormat(dto.getFormat());
        component.setText(dto.getText());
        component.setImageUrl(dto.getImageUrl());
        List<TemplateComponentButton> buttons = dto.getButtons().stream()
                .map(buttonDto -> buttonMapper.toTemplateComponentButtonEntity(buttonDto, component, template)) // pass
                                                                                                                // references
                .collect(Collectors.toList());

        component.setComponentsButtons(buttons);
        return component;

    }

    public TemplateComponent toTemplateComponentEntity(TemplateComponentRequestDto dto, Template template) {
        TemplateComponent component = new TemplateComponent();
        component.setTemplate(template);
        component.setType(dto.getType());
        component.setFormat(dto.getFormat());
        component.setText(dto.getText());
        List<TemplateComponentButton> buttons = dto.getButtons() != null
                ? dto.getButtons().stream()
                        .map(buttonDto -> buttonMapper.toTemplateComponentButtonEntity(buttonDto, component, template))
                        .collect(Collectors.toList())
                : Collections.emptyList();

        component.setComponentsButtons(buttons);
        return component;

    }
}
