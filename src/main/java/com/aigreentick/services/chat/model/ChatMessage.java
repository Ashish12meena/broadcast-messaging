package com.aigreentick.services.chat.model;


import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.aigreentick.services.chat.enums.MessageStatus;
import com.aigreentick.services.chat.enums.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage{

    @Id
    private String id;

    @Indexed
    private String chatRoomId;  // group or private room id

    @Indexed
    private Long senderId;

    @Indexed
    private Long receiverId;  // for 1-1 chats; null in group chat

    private MessageType type; // TEXT, IMAGE, FILE, VIDEO, AUDIO, etc.

    private String content;  // message text or media URL

    private Map<String, Object> metadata; // optional (file size, duration)

    private MessageStatus status; // SENT, DELIVERED, SEEN

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private boolean deletedForEveryone;

    private Set<Long> deletedForUserIds; // for soft-delete per user

    private boolean isEdited;
}

