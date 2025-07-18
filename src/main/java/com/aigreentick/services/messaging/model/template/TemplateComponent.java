package com.aigreentick.services.messaging.model.template;


import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "template_components", indexes = {
    @Index(name = "idx_template_components_template_id", columnList = "template_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateComponent {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", foreignKey = @ForeignKey(name = "fk_template_components_template"))
    @JsonIgnore
    private Template template;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "format", length = 100)
    private String format;

    @Column(name = "text", columnDefinition = "text")
    private String text;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @OneToMany(mappedBy = "component", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<TemplateComponentButton> componentsButtons;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
