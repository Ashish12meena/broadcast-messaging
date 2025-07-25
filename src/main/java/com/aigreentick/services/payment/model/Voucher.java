package com.aigreentick.services.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vouchers")
@Data
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private BigDecimal amount;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    private Integer maxUsage; // null for unlimited
    private Integer perUserLimit;

    private boolean isActive;

    private LocalDateTime createdAt;
}
