package com.aigreentick.services.auth.service.impl;

import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.model.WhatsappAppCredential;
import com.aigreentick.services.auth.repository.WhatsappAppCredentialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WhatsappAppCredentialImpl {
    private final  WhatsappAppCredentialRepository whatsappAppCredentialRepository;


}
