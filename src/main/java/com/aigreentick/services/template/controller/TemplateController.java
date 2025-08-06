package com.aigreentick.services.template.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.PaginationRequestDto;
import com.aigreentick.services.messaging.dto.ResponseStatus;
import com.aigreentick.services.template.constants.TemplateConstants;
import com.aigreentick.services.template.dto.TemplateCreateResponseDto;
import com.aigreentick.services.template.dto.TemplateResponseDto;
import com.aigreentick.services.template.dto.buildTemplate.TemplateCreateRequestDto;
import com.aigreentick.services.template.service.impl.TemplateServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates")
public class TemplateController {
    private final TemplateServiceImpl templateService;

    @PostMapping("/create")
    public ResponseEntity<?> createAuthenticationTemplate(@RequestBody @Valid TemplateCreateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        TemplateCreateResponseDto response = templateService.createTemplate(request, loginUser.getId());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), TemplateConstants.TEMPLATE_CREATED, response));
    }

    /**
     * Get all templates for the authenticated user with optional search and
     * pagination.
     */
    // @GetMapping("/my-templates")
    // public ResponseEntity<?> getUserTemplates(
    //         @RequestParam(name = "search", required = false) String search,
    //         @RequestParam(name = "status", required = false) String status,
    //         @Valid PaginationRequestDto pagination,
    //         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    //     Page<TemplateResponseDto> templates = templateService.getTemplatesByUser(customUserDetails.getId(), status, search,
    //             pagination.getPage(), pagination.getSize());
    //     return ResponseEntity.ok(new ResponseMessage<>("success", "Templates fetched successfully", templates));
    // }

}
