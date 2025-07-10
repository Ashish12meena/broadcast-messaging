package com.aigreentick.services.chat.service.interfaces;

import java.util.List;

import com.aigreentick.services.chat.model.MessageStatusLog;
import com.aigreentick.services.common.enums.MessageStatusEnum;

public interface ChatMessageStatusInerface {
    void logStatus(String messageId, Long userId, MessageStatusEnum status);
    List<MessageStatusLog> getStatusLogs(String messageId);
}