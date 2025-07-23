package com.aigreentick.services.payment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.payment.dto.WalletDto;
import com.aigreentick.services.payment.enums.TransactionStatus;
import com.aigreentick.services.payment.enums.TransactionType;
import com.aigreentick.services.payment.model.Transaction;
import com.aigreentick.services.payment.model.Wallet;
import com.aigreentick.services.payment.repository.TransactionRepository;
import com.aigreentick.services.payment.repository.WalletRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public WalletDto topUp(Long userId, BigDecimal amount, String referenceId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        Transaction txn = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SUCCESS)
                .referenceId(referenceId)
                .description("Wallet Top-Up")
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(txn);

        return mapToWalletDto(wallet);
    }

    @Transactional
    public WalletDto debit(Long userId, BigDecimal amount, String referenceId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient wallet balance.");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        Transaction txn = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.SUCCESS)
                .referenceId(referenceId)
                .description("Wallet Debit")
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(txn);

        return mapToWalletDto(wallet);
    }

    private WalletDto mapToWalletDto(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .userId(wallet.getUser().getId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .isLocked(wallet.isLocked())
                .createdAt(wallet.getCreatedAt())
                .build();
    }
}
