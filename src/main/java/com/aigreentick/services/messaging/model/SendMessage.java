package com.aigreentick.services.messaging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "send_messages", indexes = {
    @Index(name = "idx_send_messages_user_id", columnList = "user_id"),
    @Index(name = "idx_send_messages_template_id", columnList = "template_id"),
    @Index(name = "idx_send_messages_country_id", columnList = "country_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    
    @Column(name = "template_id", nullable = false)
    private Long templateId;

    
    @Column(name = "country_id", nullable = false)
    private Integer countryId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String media;

    @Column(columnDefinition = "text")
    private String message;

    // Type: 0 = text, 1 = media (based on your CHECK constraint)
    @Column(nullable = false)
    private Short type;

    // When the message was (or will be) sent
    @Column(name = "send_on")
    private OffsetDateTime sendOn;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
