package com.aigreentick.services.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherDto {
    private Long id;
    private String code;
    private BigDecimal amount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer maxUsage;
    private Integer perUserLimit;
    private boolean isActive;
}
