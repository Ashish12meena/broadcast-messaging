package com.aigreentick.services.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.payment.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    
}
