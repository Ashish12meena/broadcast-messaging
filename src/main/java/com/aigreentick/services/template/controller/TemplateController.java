package com.aigreentick.services.template.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.PaginationRequestDto;
import com.aigreentick.services.messaging.dto.ResponseStatus;
import com.aigreentick.services.template.constants.TemplateConstants;
import com.aigreentick.services.template.dto.CreateTemplateResponseDto;
import com.aigreentick.services.template.dto.FacebookApiCredentialsDto;
import com.aigreentick.services.template.dto.TemplateDto;
import com.aigreentick.services.template.dto.TemplateRequestDto;
import com.aigreentick.services.template.dto.TemplateResponseDto;
import com.aigreentick.services.template.dto.TemplateUpdateRequest;
import com.aigreentick.services.template.service.impl.TemplateServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {
    private final TemplateServiceImpl templateService;

    public TemplateController(TemplateServiceImpl templateService) {
        this.templateService = templateService;
    }

    //Create Template and send for approval
    @PostMapping("/create")
    public ResponseEntity<?> createTemplate(@RequestBody @Valid TemplateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        CreateTemplateResponseDto response = templateService.createTemplateForUser(dto, currentUser.getUsername());
        return ResponseEntity.ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(),TemplateConstants.TEMPLATE_CREATED , response));
    }

    /**
     * Get all templates for the authenticated user with optional search and
     * pagination.
     */
    @GetMapping("/my-templates")
    public ResponseEntity<?> getUserTemplates(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @Valid PaginationRequestDto pagination,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userEmail = customUserDetails.getUsername();
        Page<TemplateResponseDto> templates = templateService.getTemplatesByUser(userEmail, status, search,
                pagination.getPage(), pagination.getSize());
        return ResponseEntity.ok(new ResponseMessage<>("success", "Templates fetched successfully", templates));
    }

   

    @PutMapping("/update/{templateId}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable Long templateId,
            @RequestBody TemplateUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        templateService.updateTemplate(templateId, request, currentUser.getUsername());
        return ResponseEntity.ok("Template updated succesfully");
    }

    @DeleteMapping("/delete/{templateId}")
    public ResponseEntity<ResponseMessage<?>> deleteTemplate(
            @PathVariable Long templateId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody FacebookApiCredentialsDto credentials) {

        TemplateDto deleted = templateService.deleteUserTemplateById(
                currentUser.getUsername(),
                templateId,
                credentials);
        return ResponseEntity.ok(new ResponseMessage<>("success", "Template deleted successfully", deleted));
    }
}
