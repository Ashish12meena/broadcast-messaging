package com.aigreentick.services.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aigreentick.services.payment.enums.TransactionStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "refunds")
@Data
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Transaction originalTransaction;

    private BigDecimal refundAmount;

    private String reason;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime processedAt;
}

