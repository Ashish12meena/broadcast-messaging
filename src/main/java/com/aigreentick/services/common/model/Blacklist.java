package com.aigreentick.services.common.model;




import java.time.LocalDateTime;

import com.aigreentick.services.common.enums.BlacklistType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "blacklists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String mobile;

    @Column(name = "country_id")
    private Long countryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BlacklistType type;

    @Column(name = "sender_id", nullable = true)
    private Long senderId;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked = true;

    @Column(name = "reason")
    private String reason;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    

}

