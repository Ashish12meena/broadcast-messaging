package com.aigreentick.services.whatsapp.model;


import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "wapp_form_questions",
    indexes = {
        @Index(name = "idx_wapp_form_questions_wapp_form_id", columnList = "wapp_form_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappFormQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wapp_form_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wapp_form_questions_form"))
    private WhatsappForm wappForm;

    private String ques;

    private String type;

    @Column(name = "select_options", columnDefinition = "text")
    private String selectOptions;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

}

