package com.aigreentick.services.messaging.dto.tag;

import lombok.Data;

@Data
public class SendByTagRequestDto {
    private long templateId;
    private long tagId;
}
