package com.aigreentick.services.template.dto.buildTemplate.authentication;

import com.aigreentick.services.template.dto.buildTemplate.BaseComponentDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AuthBodyComponentDto.class, name = "BODY"),
    @JsonSubTypes.Type(value = AuthFooterComponentDto.class, name = "FOOTER"),
    @JsonSubTypes.Type(value = AuthButtonComponentDto.class, name = "BUTTONS")
})
@Data
public abstract class AuthComponentDto implements BaseComponentDto {
    private String type;
}
