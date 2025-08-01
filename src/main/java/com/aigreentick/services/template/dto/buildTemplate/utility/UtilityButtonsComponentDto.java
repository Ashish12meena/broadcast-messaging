package com.aigreentick.services.template.dto.buildTemplate.utility;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UtilityButtonsComponentDto extends UtilityComponentDto {
    private List<UtilityButtonDto> buttons;
    private UtilityButtonExampleDto example;
}
