package com.aigreentick.services.messaging.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.common.dto.BlacklistRequestDto;
import com.aigreentick.services.common.dto.BlacklistResponseDto;
import com.aigreentick.services.common.enums.BlacklistType;
import com.aigreentick.services.common.service.interfaces.BlacklistInterface;
import com.aigreentick.services.messaging.dto.template.TemplateStatsDto;
import com.aigreentick.services.messaging.model.template.Template;
import com.aigreentick.services.messaging.service.impl.TemplateServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final BlacklistInterface blacklistService;
    private final TemplateServiceImpl templateService;

    /**
     * Get a paginated list of blacklist entries with optional filters.
     * 
     * Filters:
     * - mobile (partial match)
     * - countryId
     * - type (BLOCKED_BY_USER / BLOCKED_BY_ADMIN)
     * - isBlocked (true/false)
     * - isExpired (true = expired, false = not expired)
     * 
     * Example:
     * GET
     * /admin/blacklists?mobile=9876&countryId=91&type=BLOCKED_BY_ADMIN&isExpired=false
     * 
     * Pagination:
     * - Default page size = 20
     * - Sorted by createdAt in descending order
     */
    @GetMapping("/blacklists")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<BlacklistResponseDto>> getFilteredBlacklistEntries(
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) BlacklistType type,
            @RequestParam(required = false) Boolean isBlocked,
            @RequestParam(required = false) Boolean isExpired,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BlacklistResponseDto> page = blacklistService.getFilteredBlacklistEntries(
                mobile, countryId, type, isBlocked, isExpired, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Add a new blacklist entry (ADMIN use only).
     *
     * Input required:
     * - mobile
     * - countryId
     * - reason
     *
     * Automatically sets:
     * - type = BLOCKED_BY_ADMIN
     * - isBlocked = true
     */
    @PostMapping("/blacklists")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> addToBlacklist(@RequestBody BlacklistRequestDto blacklistRequestDto) {

        return ResponseEntity.ok(blacklistService.addToBlacklistByAdmin(blacklistRequestDto));
    }

    @GetMapping("/templates")
    public ResponseEntity<Page<Template>> getFilteredTemplates(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String waId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Template> result = templateService.getAdminFilteredTemplates(
                userId, status, name, waId, category, fromDate, toDate, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/templates/stats")
    public ResponseEntity<TemplateStatsDto> getTemplateStats() {
        TemplateStatsDto stats = templateService.getTemplateStats();
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/delete/templates/{templateId}")
    public ResponseEntity<?> deleteTemplateByAdmin(@PathVariable Long templateId) {
        boolean deleted = templateService.adminDeleteTemplate(templateId);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
