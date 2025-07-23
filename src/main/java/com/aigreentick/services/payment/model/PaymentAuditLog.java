package com.aigreentick.services.payment.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payment_audit_logs")
@Data
public class PaymentAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityType; // e.g. "TRANSACTION", "WALLET"
    private Long entityId;

    private String action; // "CREATE", "UPDATE", "REVERSE"

    @Lob
    private String oldData;

    @Lob
    private String newData;

    private LocalDateTime timestamp;

    private String performedBy;
}

