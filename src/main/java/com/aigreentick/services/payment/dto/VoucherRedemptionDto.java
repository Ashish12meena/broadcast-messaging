package com.aigreentick.services.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherRedemptionDto {
    private Long id;
    private Long userId;
    private Long voucherId;
    private LocalDateTime redeemedAt;
    private BigDecimal redeemedAmount;
}

