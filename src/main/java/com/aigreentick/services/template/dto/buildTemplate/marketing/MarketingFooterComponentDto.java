package com.aigreentick.services.template.dto.buildTemplate.marketing;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MarketingFooterComponentDto extends MarketingComponentDto {
    private String text;
}