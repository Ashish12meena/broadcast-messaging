package com.aigreentick.services.messaging.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aigreentick.services.messaging.model.group.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByGroupNameAndOwner_Id(String groupName, Long userId);

    List<Group> findAllByOwner_IdAndDeletedAtIsNull(Long userId);

    Optional<Group> findByIdAndOwner_IdAndDeletedAtIsNull(Long groupId, Long userId);

    Optional<Group> findByIdAndDeletedAtIsNull(Long groupId);
    
}
