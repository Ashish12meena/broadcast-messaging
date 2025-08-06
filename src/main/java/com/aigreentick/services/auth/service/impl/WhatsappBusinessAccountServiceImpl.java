package com.aigreentick.services.auth.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.model.WhatsappBusinessAccount;
import com.aigreentick.services.auth.repository.WhatsappBusinessAccountRepository;
import com.aigreentick.services.auth.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WhatsappBusinessAccountServiceImpl {
    private final WhatsappBusinessAccountRepository wabaRepo;
    private final UserService userService;

    
    @Transactional
    public WhatsappBusinessAccount createWaba(Long userId, String wabaId,String businessName) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
                WhatsappBusinessAccount  waba = new WhatsappBusinessAccount();
        waba.setUser(user);
        waba.setBusinessName(businessName);
        waba.setUser(user);
        return wabaRepo.save(waba);
    }

    public Optional<WhatsappBusinessAccount> getWabaById(Long id) {
        return wabaRepo.findById(id);
    }

    @Transactional
    public void deleteWaba(Long id, Long userId) {
        WhatsappBusinessAccount waba = wabaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("WABA not found or not owned by user"));
        wabaRepo.delete(waba);
    }

    public WhatsappBusinessAccount findById(Long id){
        return wabaRepo.findById(id).orElseThrow(()->new IllegalArgumentException("WhatsappBusinessAccount not found with this id"));
    }
}
