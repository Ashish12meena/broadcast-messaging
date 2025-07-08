package com.aigreentick.services.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.BlacklistRequestDto;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.common.service.interfaces.BlacklistInterface;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/blacklists")
public class BlacklistController {

    @Autowired
    BlacklistInterface blacklistService;

    // // blacklist a user
    @PostMapping
    public ResponseEntity<ResponseMessage<String>> add(@RequestBody @Valid BlacklistRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user) {
        Long senderId = user.getId();
        return ResponseEntity.ok(blacklistService.addToBlacklist(request, senderId));
    }
}
