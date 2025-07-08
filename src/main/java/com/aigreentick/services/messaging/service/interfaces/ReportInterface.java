package com.aigreentick.services.messaging.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aigreentick.services.messaging.dto.ReportResponseDto;
import com.aigreentick.services.messaging.model.Report;

public interface ReportInterface {
    void sendAndUpdateReportsInControlledBatches(List<Report> reports, String template);
     Page<ReportResponseDto> getReportsByUserId(Long userId, Pageable pageable);
     Page<ReportResponseDto> getReportsByBroadcastId(Long broadCastId, Pageable pageable);
}
