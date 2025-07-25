package com.aigreentick.services.auth.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Permission {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // ✅ include in hash
    private Long id;

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore // prevent recursion in JSON
    private List<Role> roles;

    @Column(nullable = false, unique = true, length = 100)
    @EqualsAndHashCode.Include // ✅ include in hash
    private String name; // e.g. CAN_SEND_BROADCAST

    @Column(length = 255)
    @EqualsAndHashCode.Include // ✅ include in hash
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    @EqualsAndHashCode.Include // ✅ include in hash
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @EqualsAndHashCode.Include // ✅ include in hash
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @EqualsAndHashCode.Include // ✅ include in hash
    private LocalDateTime deletedAt;

}
