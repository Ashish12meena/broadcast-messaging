package com.aigreentick.services.messaging.model;


import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "send_by_tags",
    indexes = {
        @Index(name = "idx_send_by_tags_user_id", columnList = "user_id"),
        @Index(name = "idx_send_by_tags_template_id", columnList = "template_id"),
        @Index(name = "idx_send_by_tags_tag_id", columnList = "tag_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendByTag {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "is_media", nullable = false)
    private boolean isMedia = false;

    @Column(nullable = false)
    private boolean status = false;

    @Column(nullable = false)
    private int total = 0;

    @Column(name = "schedule_at")
    private ZonedDateTime scheduleAt;

    @Column(name = "ex_tag", columnDefinition = "text")
    private String exTag;

    @Column(name = "selected_camp", columnDefinition = "text")
    private String selectedCamp;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
}
