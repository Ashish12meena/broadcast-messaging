package com.aigreentick.services.messaging.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDto {
    private long pending;
    private long failed;
    private long success;
}
