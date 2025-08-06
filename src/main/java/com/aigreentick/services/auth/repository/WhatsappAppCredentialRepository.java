package com.aigreentick.services.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.model.WhatsappAppCredential;

@Repository
public interface WhatsappAppCredentialRepository extends JpaRepository<WhatsappAppCredential, Long> {
    @Modifying
    @Query("""
        UPDATE WhatsappAppCredential wc 
        SET wc.isActive = false 
        WHERE wc.waba.user.id = :userId
    """)
    void deactivateAllByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT wc FROM WhatsappAppCredential wc 
        WHERE wc.waba.user.id = :userId AND wc.isActive = true
    """)
    Optional<WhatsappAppCredential> findActiveByUserId(@Param("userId") Long userId);

    List<WhatsappAppCredential> findAllByWabaId(Long wabaId);



}
