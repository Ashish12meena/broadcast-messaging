package com.aigreentick.services.messaging.model;


import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.enums.CampaignStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "campaigns", indexes = {
        @Index(name = "idx_campaigns_user_id", columnList = "user_id"),
        @Index(name = "idx_campaigns_template_id", columnList = "template_id"),
        @Index(name = "idx_campaigns_country_id", columnList = "country_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column
    private Integer whatsapp;

    @Column(name = "country_id", nullable = false)
    private Long countryId;

    @Column(nullable = false, length = 255)
    private String campname;

    @Column(name = "is_media", nullable = false)
    private boolean isMedia = false;

    @Column(name = "col_name", length = 255)
    private String colName;

    @Column(name = "total_recipients")
    private Integer totalRecipients = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status = CampaignStatus.PENDING;

    @Column(name = "schedule_at")
    private LocalDateTime scheduleAt;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<Broadcast> broadcasts;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}

