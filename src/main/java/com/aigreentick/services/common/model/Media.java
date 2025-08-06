package com.aigreentick.services.common.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId; // e.g., upload:123456

    private String mediaHandle; // Final "h" string returned (used to send message)

    private String fileName;

    private Long fileSize;

    private String mimeType;

    private String mediaType; // IMAGE, VIDEO, DOCUMENT, AUDIO

    private String status; // e.g., PENDING, COMPLETED, FAILED

    private Long uploadedByUserId; // Optional if you need user tracking

    private String wabaId;

    private String uploadResponseJson; // full raw JSON response (for debugging)

    private LocalDateTime uploadedAt;

    private LocalDateTime completedAt;

    private Boolean isChunkedUpload; // true if uploaded in parts

    private Long fileOffset; // For resume logic (optional)

}
