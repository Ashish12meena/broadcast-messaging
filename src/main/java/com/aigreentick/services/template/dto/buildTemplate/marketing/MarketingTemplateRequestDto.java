package com.aigreentick.services.template.dto.buildTemplate.marketing;

import java.util.List;

import com.aigreentick.services.template.dto.buildTemplate.BaseTemplateRequestDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingTemplateRequestDto extends BaseTemplateRequestDto {
    private List<MarketingComponentDto> components;

     @Override
    public List<MarketingComponentDto> getComponents() {
        return components;
    }
}

