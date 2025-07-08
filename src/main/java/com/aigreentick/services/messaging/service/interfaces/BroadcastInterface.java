package com.aigreentick.services.messaging.service.interfaces;

import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.BroadcastRequestDTO;

public interface BroadcastInterface {
     ResponseMessage<String> dispatch(BroadcastRequestDTO dto, Long userId);
}
