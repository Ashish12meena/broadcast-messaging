package com.aigreentick.services.chat.service.interfaces;

import java.util.List;

import com.aigreentick.services.chat.model.ChatMessage;

public interface ChatMessageInterface {
    
    ChatMessage sendMessage(ChatMessage message);

    ChatMessage editMessage(String messageId, String newContent);

    void deleteMessageForEveryone(String messageId);

    void deleteMessageForUser(String messageId, Long userId);

    List<ChatMessage> getMessagesByRoom(String roomId);

    ChatMessage getMessageById(String messageId);
}
