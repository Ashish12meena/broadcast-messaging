package com.aigreentick.services.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.messaging.enums.BroadcastStatus;
import com.aigreentick.services.messaging.model.Broadcast;

@Repository
public interface BroadCastRepository extends JpaRepository<Broadcast, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Broadcast b SET b.status = :status, b.updatedAt = CURRENT_TIMESTAMP WHERE b.id = :id")
    void updateStatusById(@Param("status") BroadcastStatus status, @Param("id") Long id);

}
