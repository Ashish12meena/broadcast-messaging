package com.aigreentick.services.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageResult {
     private boolean success;
    private String messageId;
    private String message;
}
