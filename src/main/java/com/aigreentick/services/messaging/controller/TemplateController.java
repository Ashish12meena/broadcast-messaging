package com.aigreentick.services.messaging.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
import com.aigreentick.services.messaging.dto.template.TemplateDto;
import com.aigreentick.services.messaging.dto.template.TemplateUpdateRequest;
import com.aigreentick.services.messaging.service.impl.TemplateServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {
    private final TemplateServiceImpl templateService;

    public TemplateController(TemplateServiceImpl templateService) {
        this.templateService = templateService;
    }

    /**
     * Get all templates for the authenticated user with optional search and
     * pagination.
     */
    @GetMapping
    public ResponseEntity<?> getUserTemplates(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Principal principal) {
        String userEmail = principal.getName();
        Page<TemplateDto> templates = templateService.getTemplatesByUser(userEmail, search, page, size);
        return ResponseEntity.ok(new ResponseMessage<>("success", "Templates fetched successfully", templates));
    }

    @PostMapping
    public ResponseEntity<?> createTemplate(@RequestBody @Valid TemplateDto dto, @AuthenticationPrincipal CustomUserDetails currentUser) {
        templateService.createTemplateForUser(dto, currentUser.getUsername());
        return ResponseEntity.ok(new ResponseMessage<>("success", "Template created successfully", null));
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
    public ResponseEntity<?> deleteMyTemplate(
            @PathVariable Long templateId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        boolean deleted = templateService.SoftDeleteTemplateByIdAndUser(templateId, currentUser.getUsername());
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized or not found");
    }

    

}
