package com.aigreentick.services.messaging.model.template;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "template_component_buttons", indexes = {
    @Index(name = "idx_template_component_buttons_template_id", columnList = "template_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateComponentButton {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false, foreignKey = @ForeignKey(name = "fk_template_component_buttons_template"))
    @JsonBackReference
    private Template template;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    @JsonBackReference
    private TemplateComponent component; 

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "number", length = 20)
    private String number;

    @Column(name = "text", length = 255)
    private String text;

    @Column(name = "url", length = 255)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
