package com.aigreentick.services.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.group.SendByGroup;

@Repository
public interface SendByGroupRepository extends JpaRepository<SendByGroup, Long> {
    // Define custom query methods if needed
    
}
