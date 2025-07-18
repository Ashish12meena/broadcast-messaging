package com.aigreentick.services.messaging.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.BroadcastRequestDTO;
import com.aigreentick.services.messaging.service.interfaces.BroadcastInterface;


@RestController
@RequestMapping("/api/broadcast/")
public class BroadcastController {
    @Autowired
    private BroadcastInterface broadcastService;

    @PostMapping("/dispatch")
    public ResponseMessage<String> broadcastMessages(@RequestBody BroadcastRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getId();
        return broadcastService.dispatch(dto, userId);
    }
    
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String currentUser(@AuthenticationPrincipal CustomUserDetails user) {
        return "This is user";
    }
}