package com.aigreentick.services.chat.service.interfaces;

import java.util.List;

import com.aigreentick.services.chat.model.ChatRoom;

public interface ChatRoomInterface {
    
    ChatRoom createPrivateRoom(Long user1, Long user2);

    ChatRoom createGroupRoom(ChatRoom room);

    ChatRoom updateRoom(ChatRoom room);

    void deleteRoom(String roomId);

    ChatRoom getRoomById(String id);

    List<ChatRoom> getUserRooms(Long userId);

    void addParticipants(String roomId, List<Long> userIds);

    void removeParticipant(String roomId, Long userId);

}
