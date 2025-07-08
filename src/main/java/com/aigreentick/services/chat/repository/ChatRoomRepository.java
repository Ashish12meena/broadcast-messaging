package com.aigreentick.services.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.chat.enums.ChatRoomType;
import com.aigreentick.services.chat.model.ChatRoom;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    List<ChatRoom> findByParticipantIdsContaining(Long userId);

    Optional<ChatRoom> findByTypeAndParticipantIdsIn(ChatRoomType type, List<Long> participantIds);

    List<ChatRoom> findByIdIn(List<String> roomIds);
}
