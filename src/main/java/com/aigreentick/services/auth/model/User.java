package com.aigreentick.services.auth.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    // private String email;

    @Column(name = "mobile_no")
    private String mobileNumber;

    @Column(name = "profile_url")
    private String profilePhoto;

    @Column(name = "company_name")
    private String companyName;

    @JsonIgnore
    private String password;

    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "remember_token")
    @JsonIgnore
    private String rememberToken;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

     @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
