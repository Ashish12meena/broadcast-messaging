package com.aigreentick.services.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aigreentick.services.auth.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "wallets")
@Data
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private BigDecimal balance;

    @Column(name = "currency", length = 3)
    private String currency;

    private boolean isLocked;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

