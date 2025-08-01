package com.aigreentick.services.template.dto.buildTemplate.authentication;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthHeaderComponentDto extends AuthComponentDto {
    private String format; // TEXT only
    private String text;
    private AuthHeaderExampleDto example;
}
