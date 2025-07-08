package com.aigreentick.services.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.chat.model.ChatMessage;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(String chatRoomId);

    List<ChatMessage> findByChatRoomIdAndSenderId(String chatRoomId, Long senderId);

    List<ChatMessage> findByChatRoomId(String roomId);

    @Query("{ $text: { $search: ?0 } }")
    List<ChatMessage> searchByContent(String keyword);
}
