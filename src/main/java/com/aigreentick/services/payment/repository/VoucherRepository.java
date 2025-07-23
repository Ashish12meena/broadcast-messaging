package com.aigreentick.services.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.payment.model.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,Long> {

    Optional<Voucher> findByCodeAndIsActiveTrue(String voucherCode);
    
}
