package com.aigreentick.services.template.dto.buildTemplate;

import java.util.List;

import com.aigreentick.services.template.dto.buildTemplate.authentication.AuthTemplateRequestDto;
import com.aigreentick.services.template.dto.buildTemplate.marketing.MarketingTemplateRequestDto;
import com.aigreentick.services.template.dto.buildTemplate.utility.UtilityTemplateRequestDto;
import com.aigreentick.services.template.enums.TemplateCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AuthTemplateRequestDto.class, name = "AUTHENTICATION")   ,
    @JsonSubTypes.Type(value = MarketingTemplateRequestDto.class, name = "MARKETING"),
    @JsonSubTypes.Type(value = UtilityTemplateRequestDto.class, name = "UTILITY")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseTemplateRequestDto {
    private String name;
    private String language;
    private TemplateCategory category;

    // 👇 Polymorphic method to be overridden
    public abstract List<? extends BaseComponentDto> getComponents();
}
