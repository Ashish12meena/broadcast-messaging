package com.aigreentick.services.messaging.model.group;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.template.model.Template;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "send_by_groups", indexes = {
        @Index(name = "idx_send_by_groups_user_id", columnList = "user_id"),
        @Index(name = "idx_send_by_groups_template_id", columnList = "template_id"),
        @Index(name = "idx_send_by_groups_group_id", columnList = "group_id"),
        @Index(name = "idx_send_by_groups_country_id", columnList = "country_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendByGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User initiatedBy;

    @Column(name = "template_id")
    private String templateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "country_id")
    private Integer countryId;

    @Column(name = "is_media", nullable = false)
    private boolean isMedia = false;

    @Column(nullable = false)
    private int total = 0;

    @Column(name = "schedule_at")
    private LocalDateTime scheduleAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
