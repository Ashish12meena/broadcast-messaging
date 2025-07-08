package com.aigreentick.services.messaging.controller;


import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.TemplateDto;
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
     * Get all templates for the authenticated user with optional search and pagination.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getUserTemplates(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Principal principal
    ) {
        String userEmail = principal.getName();
        Page<TemplateDto> templates = templateService.getTemplatesByUser(userEmail, search, page, size);
        return ResponseEntity.ok(new ResponseMessage<>("success", "Templates fetched successfully", templates));
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createTemplate(@RequestBody @Valid TemplateDto dto, Principal principal) {
        String email = principal.getName();
        templateService.createTemplateForUser(dto, email);
        return ResponseEntity.ok(new ResponseMessage<>("success", "Template created successfully", null));
    }

}

