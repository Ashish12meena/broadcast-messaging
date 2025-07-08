package com.aigreentick.services.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.chat.model.MessageStatusLog;

@Repository
public interface MessageStatusLogRepository extends MongoRepository<MessageStatusLog, String> {

    List<MessageStatusLog> findByMessageId(String messageId);

    List<MessageStatusLog> findByUserId(Long userId);
}
