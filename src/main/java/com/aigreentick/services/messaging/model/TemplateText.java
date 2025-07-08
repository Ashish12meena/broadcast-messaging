package com.aigreentick.services.messaging.model;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "template_texts", indexes = {
    @Index(name = "idx_template_texts_component_id", columnList = "component_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateText {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "component_id", nullable = false, foreignKey = @ForeignKey(name = "fk_template_texts_component"))
    private TemplateComponent component;

    @Column(name = "text", nullable = false, length = 255)
    private String text;

    @Column(name = "text_index", nullable = false)
    private Integer textIndex;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
}

