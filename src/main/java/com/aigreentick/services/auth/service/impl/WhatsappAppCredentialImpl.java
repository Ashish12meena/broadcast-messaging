package com.aigreentick.services.auth.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.model.WhatsappAppCredential;
import com.aigreentick.services.auth.model.WhatsappBusinessAccount;
import com.aigreentick.services.auth.repository.WhatsappAppCredentialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WhatsappAppCredentialImpl {
    private final  WhatsappAppCredentialRepository whatsappAppCredentialRepository;
    private final WhatsappBusinessAccountServiceImpl wabaSerivice;

    @Transactional
    public WhatsappAppCredential addCredential(Long wabaId,String appId, String phoneNumberId, String accessToken) {
        WhatsappBusinessAccount waba = wabaSerivice.findById(wabaId);
        WhatsappAppCredential credential = new WhatsappAppCredential();
        credential.setWaba(waba);
        credential.setAccessToken(accessToken);
        credential.setAppId(appId);
        credential.setPhoneNumberId(phoneNumberId);
        return whatsappAppCredentialRepository.save(credential);
    }

    public List<WhatsappAppCredential> getCredentialsByWabaId(Long wabaId) {
        return whatsappAppCredentialRepository.findAllByWabaId(wabaId);

    }
    
    @Transactional
    public WhatsappAppCredential activateCredential(Long credentialId) {
        WhatsappAppCredential toActivate = whatsappAppCredentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credential not found"));

        // Deactivate all for the user
        Long userId = toActivate.getWaba().getUser().getId();
        whatsappAppCredentialRepository.deactivateAllByUserId(userId);

        toActivate.setIsActive(true);
        return whatsappAppCredentialRepository.save(toActivate);
    }

     @Transactional
    public void deactivateAllForUser(Long userId) {
       whatsappAppCredentialRepository.deactivateAllByUserId(userId);
    }

}
