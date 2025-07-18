package com.aigreentick.services.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.group.GroupMembers;

@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {
    
}
