package com.aigreentick.services.template.dto.buildTemplate.authentication;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthBodyComponentDto extends AuthComponentDto {
    private Boolean addSecurityRecommendation;
}