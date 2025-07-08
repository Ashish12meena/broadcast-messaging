package com.aigreentick.services.chat.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.aigreentick.services.chat.model.ChatMessage;
import com.aigreentick.services.chat.repository.ChatMessageRepository;
import com.aigreentick.services.chat.service.interfaces.ChatMessageInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageInterface {
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage sendMessage(ChatMessage message) {
        message.setCreatedAt(new Date().toInstant());
        return chatMessageRepository.save(message);
    }

    @Override
    public ChatMessage editMessage(String messageId, String newContent) {
        ChatMessage msg = getMessageById(messageId);
        msg.setContent(newContent);
        msg.setEdited(true);
        msg.setUpdatedAt(new Date().toInstant());
        return chatMessageRepository.save(msg);
    }

    @Override
    public void deleteMessageForEveryone(String messageId) {
        ChatMessage msg = getMessageById(messageId);
        msg.setDeletedForEveryone(true);
        chatMessageRepository.save(msg);
    }

    @Override
    public void deleteMessageForUser(String messageId, Long userId) {
        ChatMessage msg = getMessageById(messageId);
        Set<Long> deletedFor = msg.getDeletedForUserIds() == null ? new HashSet<>() : msg.getDeletedForUserIds();
        deletedFor.add(userId);
        msg.setDeletedForUserIds(deletedFor);
        chatMessageRepository.save(msg);
    }

    @Override
    public List<ChatMessage> getMessagesByRoom(String roomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId);
    }

    @Override
    public ChatMessage getMessageById(String messageId) {
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }
}
