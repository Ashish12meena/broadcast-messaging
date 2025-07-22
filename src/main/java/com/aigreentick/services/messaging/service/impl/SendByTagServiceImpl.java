package com.aigreentick.services.messaging.service.impl;

import org.springframework.stereotype.Service;

import com.aigreentick.services.messaging.dto.tag.SendByTagRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendByTagServiceImpl {
    
   public void broadcastByTag(SendByTagRequestDto request, long userId){

    }
}
