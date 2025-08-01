package com.aigreentick.services.template.dto.buildTemplate.marketing;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingButtonsComponentDto extends MarketingComponentDto {
    private List<MarketingButtonDto> buttons;
    private MarketingButtonExampleDto example;
}
