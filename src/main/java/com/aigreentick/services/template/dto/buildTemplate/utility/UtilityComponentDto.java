package com.aigreentick.services.template.dto.buildTemplate.utility;

import com.aigreentick.services.template.dto.buildTemplate.BaseComponentDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = UtilityHeaderComponentDto.class, name = "HEADER"),
    @JsonSubTypes.Type(value = UtilityBodyComponentDto.class, name = "BODY"),
    @JsonSubTypes.Type(value = UtilityFooterComponentDto.class, name = "FOOTER"),
    @JsonSubTypes.Type(value = UtilityButtonsComponentDto.class, name = "BUTTONS")
})
@Data
public abstract class UtilityComponentDto implements BaseComponentDto {
    private String type;
}
