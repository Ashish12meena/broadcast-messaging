package com.aigreentick.services.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aigreentick.services.payment.enums.TransactionStatus;
import com.aigreentick.services.payment.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // DEBIT, CREDIT

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // PENDING, SUCCESS, FAILED, REVERSED

    private String referenceId; // external txn id or system-generated

    private String description;

    private LocalDateTime createdAt;
}

