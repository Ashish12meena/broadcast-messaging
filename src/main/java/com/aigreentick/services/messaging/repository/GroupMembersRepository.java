package com.aigreentick.services.messaging.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.model.group.Group;
import com.aigreentick.services.messaging.model.group.GroupMembers;

@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {

    boolean existsByGroupAndMemberUserAndDeletedAtIsNull(Group group, User member);

    Optional<GroupMembers> findByGroup_IdAndMemberUser_Id(Long groupId, Long memberUserId);

    Optional<GroupMembers> findByIdAndGroup_Id(Long groupMemberId, Long groupId);

    List<GroupMembers> deleteAllByGroup_Id(Long groupId);
    
}
