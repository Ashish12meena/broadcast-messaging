package com.aigreentick.services.whatsapp.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.aigreentick.services.auth.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "whatsapp_accounts", indexes = {
    @Index(name = "idx_whatsapp_user_id", columnList = "user_id"),
    @Index(name = "idx_whatsapp_created_by", columnList = "created_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Creator of the WhatsApp account entry (admin or system user)
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "whatsapp_no", nullable = false, length = 20)
    private String whatsappNo;

    @Column(name = "whatsapp_no_id", nullable = false, length = 100)
    private String whatsappNoId;

    @Column(name = "whatsapp_biz_id", nullable = false, length = 100)
    private String whatsappBizId;

    // Permanent access token for API communication
    @Column(name = "parmenent_token", nullable = false, length = 512)
    private String parmenentToken;

    // Current access token (may expire)
    @Column(length = 512)
    private String token;

    @Min(0)
    @Max(2)
    @Column(nullable = false)
    private Short status;     // Status: 0 = inactive, 1 = active, 2 = error/disabled

    @Column(columnDefinition = "TEXT")
    private String response;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}

