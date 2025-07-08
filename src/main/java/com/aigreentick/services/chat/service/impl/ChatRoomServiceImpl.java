package com.aigreentick.services.chat.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.aigreentick.services.chat.enums.ChatRoomType;
import com.aigreentick.services.chat.model.ChatRoom;
import com.aigreentick.services.chat.repository.ChatRoomRepository;
import com.aigreentick.services.chat.service.interfaces.ChatRoomInterface;

@Service
public class ChatRoomServiceImpl implements ChatRoomInterface {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public ChatRoom createPrivateRoom(Long user1, Long user2) {
        List<Long> participants = Arrays.asList(user1, user2);
        Optional<ChatRoom> existing = chatRoomRepository.findByTypeAndParticipantIdsIn(ChatRoomType.PRIVATE,
                participants);
        return existing.orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                .type(ChatRoomType.PRIVATE)
                .participantIds(participants)
                .createdBy(user1)
                .createdAt(new Date().toInstant())
                .build()));
    }

    @Override
    public ChatRoom createGroupRoom(ChatRoom room) {
        room.setType(ChatRoomType.GROUP);
        room.setCreatedAt(new Date().toInstant());
        return chatRoomRepository.save(room);
    }

    @Override
    public ChatRoom updateRoom(ChatRoom room) {
           room.setUpdatedAt(new Date().toInstant());
        return chatRoomRepository.save(room);
    }

    @Override
    public void deleteRoom(String roomId) {
           chatRoomRepository.deleteById(roomId);
    }

    @Override
    public ChatRoom getRoomById(String id) {
        return chatRoomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public List<ChatRoom> getUserRooms(Long userId) {
        return chatRoomRepository.findByParticipantIdsContaining(userId);
    }

    @Override
    public void addParticipants(String roomId, List<Long> userIds) {
        ChatRoom room = getRoomById(roomId);
        Set<Long> updated = new HashSet<>(room.getParticipantIds());
        updated.addAll(userIds);
        room.setParticipantIds(new ArrayList<>(updated));
        chatRoomRepository.save(room);
    }

    @Override
    public void removeParticipant(String roomId, Long userId) {
         ChatRoom room = getRoomById(roomId);
        room.getParticipantIds().remove(userId);
        chatRoomRepository.save(room);
    }

}

