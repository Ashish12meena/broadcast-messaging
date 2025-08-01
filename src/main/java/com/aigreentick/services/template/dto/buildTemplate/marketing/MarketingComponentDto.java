package com.aigreentick.services.template.dto.buildTemplate.marketing;

import com.aigreentick.services.template.dto.buildTemplate.BaseComponentDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MarketingHeaderComponentDto.class, name = "HEADER"),
    @JsonSubTypes.Type(value = MarketingBodyComponentDto.class, name = "BODY"),
    @JsonSubTypes.Type(value = MarketingFooterComponentDto.class, name = "FOOTER"),
    @JsonSubTypes.Type(value = MarketingButtonsComponentDto.class, name = "BUTTONS")
})
@Data
public abstract class MarketingComponentDto implements BaseComponentDto{
    private String type;
}
