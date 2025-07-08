package com.aigreentick.services.messaging.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.messaging.enums.CampaignStatus;
import com.aigreentick.services.messaging.model.Campaign;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByUserId(Long userId);

    List<Campaign> findByStatus(CampaignStatus status);

    List<Campaign> findByDeletedAtIsNull();

    @Query("SELECT c FROM Campaign c WHERE c.status = :status AND c.scheduleAt <= :now AND c.deletedAt IS NULL")
    List<Campaign> findScheduledCampaignsToRun(CampaignStatus status, LocalDateTime now);

    boolean existsByCampnameAndUserId(String campname, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Campaign c SET c.status = :status WHERE c.id = :id")
    int updateCampaignStatusById(@Param("id") Long id, @Param("status") CampaignStatus status);
}