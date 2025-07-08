package com.aigreentick.services.messaging.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.TemplateComponentButtonDto;
import com.aigreentick.services.messaging.model.Template;
import com.aigreentick.services.messaging.model.TemplateComponent;
import com.aigreentick.services.messaging.model.TemplateComponentButton;

@Component
public class TemplateComponentButtonMapper {
     public TemplateComponentButtonDto toTemplateComponentButtonDto(TemplateComponentButton button) {
        TemplateComponentButtonDto dto = new TemplateComponentButtonDto();
        dto.setId(button.getId());
        dto.setTemplateId(button.getTemplate().getId());
        dto.setComponentId(button.getComponent().getId());
        dto.setType(button.getType());
        dto.setNumber(button.getNumber());
        dto.setText(button.getText());
        dto.setUrl(button.getUrl());
        return dto;
    }

    public TemplateComponentButton toTemplateComponentButtonEntity(TemplateComponentButtonDto dto,TemplateComponent component,Template template){
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
