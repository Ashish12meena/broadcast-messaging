package com.aigreentick.services.template.dto.buildTemplate.utility;

import java.util.List;

import com.aigreentick.services.template.dto.buildTemplate.BaseTemplateRequestDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UtilityTemplateRequestDto extends BaseTemplateRequestDto {
    private List<UtilityComponentDto> components;

    @Override
    public List<UtilityComponentDto> getComponents() {
        return components;
    }
}
