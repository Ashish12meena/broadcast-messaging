package com.aigreentick.services.auth.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_permissions")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @EqualsAndHashCode.Include
    private boolean granted = true; // true = allow, false = revoke

    @Column(name = "created_at")
    @EqualsAndHashCode.Include
    private LocalDateTime createdAt;
}

