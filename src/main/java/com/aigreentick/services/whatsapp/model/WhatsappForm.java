package com.aigreentick.services.whatsapp.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.aigreentick.services.auth.model.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "wapp_forms",
    indexes = {
        @Index(name = "idx_wapp_forms_user_id", columnList = "user_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappForm {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wapp_forms_user"))
    private User user;

    @Column(name = "form_name", nullable = false, length = 255)
    private String formName;

    @Column(length = 255)
    private String action;

    @Column(name = "is_webhook", nullable = false)
    private boolean isWebhook = false;

    @Column(name = "webhook_url", length = 255)
    private String webhookUrl;

    @Column(name = "is_duplicate", nullable = false)
    private boolean isDuplicate = false;

    @Column(length = 255)
    private String notes;

    @Column(name = "form_response", length = 522)
    private String formResponse;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
    
}
