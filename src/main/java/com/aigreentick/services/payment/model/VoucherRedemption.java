package com.aigreentick.services.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aigreentick.services.auth.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "voucher_redemptions")
@Data
@Builder
public class VoucherRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Voucher voucher;

    private LocalDateTime redeemedAt;

    private BigDecimal redeemedAmount;
}

