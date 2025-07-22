package com.aigreentick.services.template.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.template.dto.TemplateComponentButtonRequestDto;
import com.aigreentick.services.template.dto.TemplateComponentButtonResponseDto;
import com.aigreentick.services.template.model.Template;
import com.aigreentick.services.template.model.TemplateComponent;
import com.aigreentick.services.template.model.TemplateComponentButton;

@Component
public class TemplateComponentButtonMapper {
     public TemplateComponentButtonResponseDto toTemplateComponentButtonDto(TemplateComponentButton button) {
        TemplateComponentButtonResponseDto dto = new TemplateComponentButtonResponseDto();
        dto.setId(button.getId());
        dto.setTemplateId(button.getTemplate().getId());
        dto.setComponentId(button.getComponent().getId());
        dto.setType(button.getType());
        dto.setNumber(button.getNumber());
        dto.setText(button.getText());
        dto.setUrl(button.getUrl());
        return dto;
    }

    public TemplateComponentButton toTemplateComponentButtonEntity(TemplateComponentButtonResponseDto dto,TemplateComponent component,Template template){
        TemplateComponentButton templateComponentButton = new TemplateComponentButton();
        templateComponentButton.setNumber(dto.getNumber());
        templateComponentButton.setComponent(component);
        templateComponentButton.setType(dto.getType());
        templateComponentButton.setTemplate(template);
        templateComponentButton.setText(dto.getText());
        templateComponentButton.setUrl(dto.getUrl());
        return templateComponentButton;
    }
    public TemplateComponentButton toTemplateComponentButtonEntity(TemplateComponentButtonRequestDto dto,TemplateComponent component,Template template){
        TemplateComponentButton templateComponentButton = new TemplateComponentButton();
        templateComponentButton.setNumber(dto.getNumber());
        templateComponentButton.setComponent(component);
        templateComponentButton.setType(dto.getType());
        templateComponentButton.setTemplate(template);
        templateComponentButton.setText(dto.getText());
        templateComponentButton.setUrl(dto.getUrl());
        return templateComponentButton;
    }
}
