package com.aigreentick.services.messaging.model.tag;

import java.time.LocalDateTime;

import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.enums.TagStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags", indexes = {
        @Index(name = "idx_tags_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who created the tag
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "tag_color", nullable = false, length = 20)
    private String tagColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TagStatus status;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TagNumber> numbers;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TagKeyword> keywords;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
