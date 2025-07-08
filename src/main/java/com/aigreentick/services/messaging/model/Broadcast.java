package com.aigreentick.services.messaging.model;

import java.time.LocalDateTime;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.enums.BroadcastStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "broadcasts", indexes = {
        @Index(name = "idx_broadcasts_country_id", columnList = "country_id"),
        @Index(name = "idx_broadcasts_template_id", columnList = "template_id"),
        @Index(name = "idx_broadcasts_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Broadcast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private Template template;

    @Column(name = "campany_name")
    private String campanyName;

    @Column(name = "whatsapp")
    private Integer whatsapp;

    @Column(name = "is_media")
    private boolean isMedia = false;

    @Column(columnDefinition = "text")
    private String data;

    @Column(name = "total_sent", nullable = false)
    private Integer totalSent = 0;

    @Column(name = "total_failed", nullable = false)
    private Integer totalFailed = 0;

    @Column(name = "schedule_at")
    private LocalDateTime scheduleAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BroadcastStatus status = BroadcastStatus.SCHEDULED;

    @Lob
    @Column(name = "recipients")
    private String recipients; 

    @Lob
    @Column(name = "payload")
    private String payload; 

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    @JsonIgnore
    private LocalDateTime deletedAt;

}
