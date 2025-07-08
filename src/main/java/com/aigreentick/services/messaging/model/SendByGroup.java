package com.aigreentick.services.messaging.model;


import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "send_by_groups",
    indexes = {
        @Index(name = "idx_send_by_groups_user_id", columnList = "user_id"),
        @Index(name = "idx_send_by_groups_template_id", columnList = "template_id"),
        @Index(name = "idx_send_by_groups_group_id", columnList = "group_id"),
        @Index(name = "idx_send_by_groups_country_id", columnList = "country_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendByGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "country_id")
    private Integer countryId;

    @Column(name = "is_media", nullable = false)
    private boolean isMedia = false;

    @Column(nullable = false)
    private int total = 0;

    @Column(nullable = false)
    private boolean status = false;

    @Column(name = "schedule_at")
    private ZonedDateTime scheduleAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
}
