package com.aigreentick.services.payment.model;

import java.time.LocalDateTime;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.payment.enums.PaymentMethodType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payment_methods")
@Data
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType type; // CARD, UPI, WALLET, BANK

    private String details; // e.g. masked card, UPI ID

    private boolean isDefault;

    private LocalDateTime addedAt;
}

