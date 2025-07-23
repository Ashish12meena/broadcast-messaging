package com.aigreentick.services.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.payment.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long>{

    Optional<Wallet> findByUserId(Long userId);
    
}
