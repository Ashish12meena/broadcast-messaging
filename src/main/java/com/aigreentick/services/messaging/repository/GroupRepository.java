package com.aigreentick.services.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.group.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
}
