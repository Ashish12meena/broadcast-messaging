package com.aigreentick.services.messaging.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.BroadcastRequestDTO;
import com.aigreentick.services.messaging.service.interfaces.BroadcastInterface;

@Controller
public class BroadcastController {
    @Autowired
    private BroadcastInterface broadcastService;

    @MutationMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseMessage<String> broadcastMessages(@Argument("broadCastInput") BroadcastRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getId();
        return broadcastService.dispatch(dto, userId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String currentUser(@AuthenticationPrincipal CustomUserDetails user) {
        return "This is user";
    }
}