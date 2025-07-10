package com.aigreentick.services.messaging.dto.template;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateStatsDto {
    private long total;
    private Map<String, Long> statusCounts;
    private Map<String, Long> categoryCounts;
}
