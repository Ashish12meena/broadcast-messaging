package com.aigreentick.services.messaging.model.tag;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tag_numbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Phone number or contact identifier associated with the tag
    @Column(nullable = false)
    private String number;

    // Parent tag reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // Soft delete timestamp
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
