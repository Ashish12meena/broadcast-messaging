package com.aigreentick.services.messaging.controller;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.messaging.dto.report.ReportResponseDto;
import com.aigreentick.services.messaging.dto.report.ReportSummaryDto;
import com.aigreentick.services.messaging.service.impl.ReportServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportServiceImpl reportService;

    @GetMapping("/user/filtered")
    public ResponseEntity<Page<ReportResponseDto>> getFilteredReportForLoginUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        Page<ReportResponseDto> reports = reportService.getFilteredReportsForUser(
                currentUser.getId(), page, size, status, type, fromDate, toDate);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/my/summary")
    public ResponseEntity<ReportSummaryDto> getMyReportSummary(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ReportSummaryDto summary = reportService.getReportSummaryForUser(currentUser.getId(), status, type, fromDate ,toDate);
        return ResponseEntity.ok(summary);
    }

    

}
