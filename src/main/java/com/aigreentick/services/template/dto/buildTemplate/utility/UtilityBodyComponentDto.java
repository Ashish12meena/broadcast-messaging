package com.aigreentick.services.template.dto.buildTemplate.utility;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UtilityBodyComponentDto extends UtilityComponentDto {
    private String text;
    private UtilityBodyExampleDto example;
}
