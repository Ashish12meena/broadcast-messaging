package com.aigreentick.services.messaging.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ReportResponseDto;
import com.aigreentick.services.messaging.service.impl.ReportServiceImpl;


@RestController
@RequestMapping("/admin/reports")
public class AdminReportController {
    @Autowired
    private ReportServiceImpl reportService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getReportsByUserId(@PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponseDto> reports = reportService.getReportsByUserId(userId, pageable);
        return ResponseEntity.ok(new ResponseMessage<>("success",
                "List of Reports Fetched By User Id",
                reports));
    }

    @GetMapping("/broadcast/{broadcastId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getReportsByBroadcastId(@PathVariable Long broadcastId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponseDto> reports = reportService.getReportsByBroadcastId(broadcastId, pageable);
        return ResponseEntity.ok(new ResponseMessage<>("success",
                "List of Reports Fetched By BroadCast Id",
                reports));
    }
}

