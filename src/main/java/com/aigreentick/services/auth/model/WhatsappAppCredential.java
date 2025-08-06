package com.aigreentick.services.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
@Table(name = "whatsapp_app_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsappAppCredential {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appId;

    private String accessToken; // Store securely (e.g., encrypted or in a vault)

    private String phoneNumberId;

    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waba_id")
     @JsonBackReference
    private WhatsappBusinessAccount waba;
}
