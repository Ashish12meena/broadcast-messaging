package com.aigreentick.services.template.dto.buildTemplate.authentication;

import lombok.Data;


@Data
public class SupportedAppDto {
    private String packageName;
    private String signatureHash;
}
