package com.aigreentick.services.chat.service.interfaces;

import java.util.List;

import com.aigreentick.services.chat.enums.MessageStatus;
import com.aigreentick.services.chat.model.MessageStatusLog;

public interface ChatMessageStatusInerface {
    void logStatus(String messageId, Long userId, MessageStatus status);
    List<MessageStatusLog> getStatusLogs(String messageId);
}