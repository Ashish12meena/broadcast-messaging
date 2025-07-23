package com.aigreentick.services.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aigreentick.services.payment.enums.TransactionStatus;
import com.aigreentick.services.payment.enums.TransactionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDto {
    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String referenceId;
    private String description;
    private LocalDateTime createdAt;
}
