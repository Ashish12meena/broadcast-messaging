package com.aigreentick.services.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.model.WhatsappBusinessAccount;

@Repository
public interface WhatsappBusinessAccountRepository extends JpaRepository<WhatsappBusinessAccount,Long> {
    
}
