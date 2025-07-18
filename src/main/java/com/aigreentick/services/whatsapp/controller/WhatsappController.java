package com.aigreentick.services.whatsapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ResponseStatus;
import com.aigreentick.services.whatsapp.dto.SendMessageResult;
import com.aigreentick.services.whatsapp.dto.WhatsAppTemplateMessageRequest;
import com.aigreentick.services.whatsapp.service.impl.WhatsappServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
public class WhatsappController {
    private final WhatsappServiceImpl whatsappService;

    // @PostMapping("/send-template")
    // public ResponseEntity<?> createTemplate(@RequestBody @Valid
    // TemplateRequestDto dto, @AuthenticationPrincipal CustomUserDetails
    // currentUser) {
    // whatsappService.submitTemplateToFacebook(dto);
    // return ResponseEntity.ok(new ResponseMessage<>("success", "Template created
    // successfully", null));
    // }

    @PostMapping("/send-template")
    public ResponseEntity<ResponseMessage<String>> sendTemplateMessage(
            @Valid @RequestBody WhatsAppTemplateMessageRequest request) {
        SendMessageResult result = whatsappService.sendTemplateMessage(
                request.getRecipientNumber(),
                request.getTemplateName(),
                request.getLanguageCode(),
                request.getParametersJson(),
                request.getAccessToken(),
                request.getApiVersion(),
                request.getWhatsappId());

        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ResponseMessage<>(ResponseStatus.ERROR.name(), result.getMessage(), null));
        }
        return ResponseEntity.ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), result.getMessage(), result.getMessageId()));
    }

}
