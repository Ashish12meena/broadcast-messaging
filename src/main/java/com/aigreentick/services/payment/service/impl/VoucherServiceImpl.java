package com.aigreentick.services.payment.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.payment.dto.VoucherRedemptionDto;
import com.aigreentick.services.payment.model.Voucher;
import com.aigreentick.services.payment.model.VoucherRedemption;
import com.aigreentick.services.payment.repository.VoucherRedemptionRepository;
import com.aigreentick.services.payment.repository.VoucherRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl {

    private final VoucherRepository voucherRepository;
    private final VoucherRedemptionRepository redemptionRepository;
    private final UserService userService;

    @Transactional
    public VoucherRedemptionDto redeemVoucher(Long userId, String voucherCode) {
        Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(voucherCode)
                .orElseThrow(() -> new EntityNotFoundException("Invalid or expired voucher"));

        if (voucher.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Voucher has expired.");
        }

        long userUsageCount = redemptionRepository.countByUserIdAndVoucherId(userId, voucher.getId());
        if (voucher.getPerUserLimit() != null && userUsageCount >= voucher.getPerUserLimit()) {
            throw new IllegalStateException("User limit for voucher exceeded.");
        }

        long totalRedemptions = redemptionRepository.countByVoucherId(voucher.getId());
        if (voucher.getMaxUsage() != null && totalRedemptions >= voucher.getMaxUsage()) {
            throw new IllegalStateException("Voucher usage limit reached.");
        }

        VoucherRedemption redemption = VoucherRedemption.builder()
                .user(userService.getReferenceById(userId))
                .voucher(voucher)
                .redeemedAt(LocalDateTime.now())
                .redeemedAmount(voucher.getAmount())
                .build();

        redemptionRepository.save(redemption);

        return VoucherRedemptionDto.builder()
                .id(redemption.getId())
                .userId(userId)
                .voucherId(voucher.getId())
                .redeemedAmount(voucher.getAmount())
                .redeemedAt(redemption.getRedeemedAt())
                .build();
    }
}
