package com.aigreentick.services.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.payment.model.VoucherRedemption;

@Repository
public interface VoucherRedemptionRepository extends JpaRepository<VoucherRedemption,Long>{

    long countByUserIdAndVoucherId(Long userId, Long id);

    long countByVoucherId(Long id);
    
}
