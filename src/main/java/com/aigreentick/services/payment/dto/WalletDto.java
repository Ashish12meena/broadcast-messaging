package com.aigreentick.services.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletDto {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private boolean isLocked;
    private LocalDateTime createdAt;
}

