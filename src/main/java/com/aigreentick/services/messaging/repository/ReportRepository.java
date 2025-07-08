package com.aigreentick.services.messaging.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.Report;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {

    Page<Report> findByUserId(Long userId, Pageable pageable);

    Page<Report> findByBroadcastId(Long broadcastId, Pageable pageable);

}
