package com.aigreentick.services.messaging.controller;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.report.ReportResponseDto;
import com.aigreentick.services.messaging.model.Report;
import com.aigreentick.services.messaging.service.impl.ReportServiceImpl;

@RestController
@RequestMapping("/admin/reports")
public class AdminReportController {
        @Autowired
        private ReportServiceImpl reportService;

        @GetMapping("/user/{userId}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
        public ResponseEntity<?> getReportsByUserId(
                        @PathVariable Long userId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
                Page<ReportResponseDto> reports = reportService.getFilteredReportsForUser(userId, page, size, status,
                                type,
                                fromDate, toDate);
                return ResponseEntity.ok(new ResponseMessage<>("success",
                                "List of Reports Fetched By User Id",
                                reports));
        }

        // Sample:- GET /admin/reports?userId=123&type=OTP&status=FAILED&page=0&size=50
        @GetMapping("/filtered")
        public ResponseEntity<Page<ReportResponseDto>> getAdminReports(
                        @RequestParam(required = false) Long userId,
                        @RequestParam(required = false) Long broadcastId,
                        @RequestParam(required = false) Long campaignId,
                        @RequestParam(required = false) String tagLogId,
                        @RequestParam(required = false) String messageId,
                        @RequestParam(required = false) String status,
                             @RequestParam(required = false) String type,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {
                Page<ReportResponseDto> reports = reportService.getFilteredReportsForAdmin(
                                userId, broadcastId, campaignId, tagLogId, messageId,
                                status, type, fromDate, toDate, page, size);
                return ResponseEntity.ok(reports);
        }

        @GetMapping("/campaign/{campaignId}")
        public ResponseEntity<Page<ReportResponseDto>> getMyReportsByCampaign(
                        @PathVariable String campaignId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
                Page<ReportResponseDto> reports = reportService.getFilteredReportsByCampaign(
                                campaignId, page, size, status, type, fromDate, toDate);
                return ResponseEntity.ok(reports);
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

        @DeleteMapping("/delete/{reportId}")
        public ResponseEntity<?> deleteReportById(@PathVariable String reportId) {
                boolean deleted = reportService.softDeleteReportById(reportId);
                if (deleted) {
                        return ResponseEntity.noContent().build(); // 204 No Content
                } else {
                        return ResponseEntity.notFound().build(); // 404 Not Found
                }
        }

        // sample:- POST
        // /admin/reports/bulk-delete?userId=abc123&campaignId=cmp456&type=OTP&status=FAILED&fromDate=2025-07-01T00:00:00Z&toDate=2025-07-08T23:59:59Z
        @PostMapping("/delete/filtered")
        public ResponseEntity<Map<String, Long>> bulkSoftDelete(
                        @RequestParam(required = false) String userId,
                        @RequestParam(required = false) String campaignId,
                        @RequestParam(required = false) String broadcastId,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
                long deletedCount = reportService.bulkSoftDeleteByFilter(
                                userId, campaignId, broadcastId, status, type, fromDate, toDate);
                return ResponseEntity.ok(Map.of("deletedCount", deletedCount));
        }

        // Sample:- GET /internal/reports/pending?status=FAILED&retryable=true
        @GetMapping("/pending")
        public ResponseEntity<List<Report>> getPendingRetryableReports(
                        @RequestParam(defaultValue = "FAILED") String status,
                        @RequestParam(defaultValue = "true") boolean retryable) {
                List<Report> reports = reportService.getPendingRetryableReports(status, retryable);
                return ResponseEntity.ok(reports);
        }

        @PatchMapping("/update/reports/status-by-messageId")
        public ResponseEntity<?> updateReportStatus(@RequestBody Map<String, Object> body) {
                String messageId = (String) body.get("messageId");
                String status = (String) body.get("status");
                String updatedAtStr = (String) body.get("updatedAt");

                if (messageId == null || status == null) {
                        return ResponseEntity.badRequest().body("messageId and status are required");
                }

                Instant updatedAt = null;
                if (updatedAtStr != null) {
                        try {
                         updatedAt = Instant.parse(updatedAtStr);
                        } catch (DateTimeParseException e) {
                                return ResponseEntity.badRequest().body("Invalid updatedAt format. Use ISO 8601.");
                        }
                }

                boolean updated = reportService.updateReportStatusByMessageId(messageId, status, updatedAt);
                return updated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        }

}
