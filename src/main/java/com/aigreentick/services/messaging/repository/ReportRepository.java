package com.aigreentick.services.messaging.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.Report;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {

    Page<Report> findByUserId(Long userId, Pageable pageable);

    Page<Report> findByBroadcastId(Long broadcastId, Pageable pageable);
    List<Report> findByCampaignIdAndUserId(Long campaignId, Long userId);
    List<Report> findByBroadcastIdAndUserId(Long broadcastId, Long userId);
    List<Report> findByTagLogIdAndUserId(Long tagLogId, Long userId);
    Optional<Report> findByMessageId(String messageId);
    List<Report> findByMobile(String mobile);
    List<Report> findByUserIdAndDeletedAtIsNull(Long userId);

}
