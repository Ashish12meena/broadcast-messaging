package com.aigreentick.services.template.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ResponseStatus;
import com.aigreentick.services.template.constants.TemplateConstants;
import com.aigreentick.services.template.dto.TemplateCreateResponseDto;
import com.aigreentick.services.template.dto.buildTemplate.TemplateCreateRequestDto;
import com.aigreentick.services.template.service.impl.AuthenticationTemplateServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates/authentication")
public class AuthenticationTemplateController {
    private final AuthenticationTemplateServiceImpl authenticationTemplateService;

    @PostMapping("/create")
    public ResponseEntity<?> createAuthenticationTemplate(@RequestBody @Valid TemplateCreateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        TemplateCreateResponseDto response = authenticationTemplateService.createTemplate(request, loginUser.getId());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), TemplateConstants.TEMPLATE_CREATED, response));
    }
}
