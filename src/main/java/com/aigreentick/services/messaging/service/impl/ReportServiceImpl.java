package com.aigreentick.services.messaging.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.aigreentick.services.common.enums.MessageStatusEnum;
import com.aigreentick.services.messaging.constants.MessagingConstants;
import com.aigreentick.services.messaging.dto.report.ReportResponseDto;
import com.aigreentick.services.messaging.dto.report.ReportSummaryDto;
import com.aigreentick.services.messaging.enums.ReportStatus;
import com.aigreentick.services.messaging.mapper.ReportMapper;
import com.aigreentick.services.messaging.model.Report;
import com.aigreentick.services.messaging.repository.ReportRepository;
import com.aigreentick.services.messaging.service.interfaces.ReportInterface;
import com.mongodb.client.result.UpdateResult;

@Service
public class ReportServiceImpl implements ReportInterface {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final MongoTemplate mongoTemplate;
    private final ExecutorService whatsappExecutor;
    private final Semaphore semaphore;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportServiceImpl( MongoTemplate mongoTemplate,
            @Qualifier("whatsappExecutor") ExecutorService whatsappExecutor,
            Semaphore whatsappConcurrencySemaphore,
            ReportRepository reportRepository,
            ReportMapper reportMapper) {
        this.mongoTemplate = mongoTemplate;
        this.whatsappExecutor = whatsappExecutor;
        this.semaphore = whatsappConcurrencySemaphore;
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    /**
     * Processes reports concurrently with a semaphore controlling the max parallel
     * requests.
     * Then updates them in bulk in MongoDB.
     */
    public void sendAndUpdateReportsInControlledBatches(List<Report> reports, String template) {
        final Queue<Report> buffer = new ConcurrentLinkedQueue<>();
        final Object lock = new Object();
        final CountDownLatch latch = new CountDownLatch(reports.size());
        for (Report report : reports) {
            whatsappExecutor.execute(() -> {
                try {
                    semaphore.acquire();
                    Report updatedReport = processReport(report, template);
                    buffer.add(updatedReport);

                    // Batch flush logic
                    synchronized (lock) {
                        if (buffer.size() >= MessagingConstants.MAX_CONCURRENT_WHATSAPP_REQUESTS) {
                            List<Report> batch = new ArrayList<>();
                            for (int i = 0; i < MessagingConstants.MAX_CONCURRENT_WHATSAPP_REQUESTS; i++) {
                                Report r = buffer.poll();
                                if (r != null) {
                                    batch.add(r);
                                }
                            }
                            if (!batch.isEmpty()) {
                                bulkUpdateReports(batch);
                                log.info("Batch of {} reports updated", batch.size());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Interrupted while processing report {}", report.getId(), e);
                } finally {
                    semaphore.release();
                    latch.countDown();
                }
            });        }

        try {
            latch.await(); // wait for all tasks to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Latch await interrupted", e);
        }
        
        // Final flush for leftover reports
        if (!buffer.isEmpty()) {
            List<Report> finalBatch = new ArrayList<>();
            while (!buffer.isEmpty()) {
                finalBatch.add(buffer.poll());
            }
            bulkUpdateReports(finalBatch);
            log.info("Final batch of {} reports updated", finalBatch.size());
        }

        log.info("All reports processed and updated.");
    }

    /**
     * Saves all report chunks asynchronously or in sequence.
     */
    public void saveReportChunks(List<List<Report>> chunkedReports) {
        for (List<Report> chunk : chunkedReports) {
            reportRepository.saveAll(chunk);
            log.info("Saved {} reports in database", chunk.size());
        }
    }

    public Page<ReportResponseDto> getReportsByUserId(Long userId, Pageable pageable) {
        Page<Report> reportPage = reportRepository.findByUserId(userId, pageable);
        return reportPage.map(reportMapper::toReportDto);
    }

    public Page<ReportResponseDto> getFilteredReportsForUser(Long userId, int page, int size, String status,
            String type,
            Instant fromDate, Instant toDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Criteria criteria = Criteria.where("userId").is(userId);

        if (status != null) {
            criteria.and("status").is(status);
        }
        if (type != null) {
            criteria.and("type").is(type);
        }
        if (fromDate != null && toDate != null) {
            criteria.and("createdAt").gte(fromDate).lte(toDate);
        } else if (fromDate != null) {
            criteria.and("createdAt").gte(fromDate);
        } else if (toDate != null) {
            criteria.and("createdAt").lte(toDate);
        }
        Query query = new Query(criteria).with(pageable);
        List<Report> reports = mongoTemplate.find(query, Report.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Report.class);

        List<ReportResponseDto> dtos = reports.stream()
                .map(reportMapper::toReportDto)
                .toList();

        return new PageImpl<>(dtos, pageable, count);
    }

    public Page<ReportResponseDto> getFilteredReportsByCampaign(
            String campaignId,
            int page,
            int size,
            String status,
            String type,
            Instant fromDate,
            Instant toDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Criteria criteria = Criteria.where("campaignId").is(campaignId);

        if (status != null) {
            criteria.and("status").is(status);
        }
        if (type != null) {
            criteria.and("type").is(type);
        }
        if (fromDate != null && toDate != null) {
            criteria.and("createdAt").gte(fromDate).lte(toDate);
        } else if (fromDate != null) {
            criteria.and("createdAt").gte(fromDate);
        } else if (toDate != null) {
            criteria.and("createdAt").lte(toDate);
        }

        Query query = new Query(criteria).with(pageable);
        List<Report> reports = mongoTemplate.find(query, Report.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Report.class);

        List<ReportResponseDto> dtos = reports.stream()
                .map(reportMapper::toReportDto)
                .toList();

        return new PageImpl<>(dtos, pageable, total);
    }

    @Override
    public Page<ReportResponseDto> getReportsByBroadcastId(Long broadCastId, Pageable pageable) {
        Page<Report> reports = reportRepository.findByBroadcastId(broadCastId, pageable);
        return reports.map(reportMapper::toReportDto);
    }

    private Report processReport(Report report, String template) {
        try {
            // Map<String, Object> response = whatsappService.sendMessage(report.getMobile(), template);
            // report.setMessageStatus((MessageStatusEnum)response.get("status"));
            // report.setMessageId((String) response.get("messageId"));
            // report.setWaId((String) response.get("waId"));
            // report.setStatus(
            //     MessageStatusEnum.DELIVERED == report.getMessageStatus()
            //         ? ReportStatus.SUCCESS
            //         : ReportStatus.FAILED
            // );
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to {}", report.getMobile(), e);
            report.setStatus(ReportStatus.FAILED);
            report.setMessageStatus(MessageStatusEnum.FAILED);
        }
        report.setUpdatedAt(Instant.now());
        return report;
    }

    private void bulkUpdateReports(List<Report> reports) {
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Report.class);
        for (Report r : reports) {
            Query query = new Query(Criteria.where("_id").is(r.getId()));
            Update update = new Update()
                    .set("status", r.getStatus())
                    .set("messageStatus", r.getMessageStatus())
                    .set("messageId", r.getMessageId())
                    .set("waId", r.getWaId())
                    .set("updatedAt", r.getUpdatedAt());
            ops.updateOne(query, update);
        }
        ops.execute();
    }

    public ReportSummaryDto getReportSummaryForUser(
            Long userId,
            String status,
            String type,
            Instant fromDate,
            Instant toDate) {

        Criteria baseCriteria = Criteria.where("userId").is(userId);

        if (type != null) {
            baseCriteria.and("type").is(type);
        }
        if (fromDate != null && toDate != null) {
            baseCriteria.and("createdAt").gte(fromDate).lte(toDate); // ✅ fixed here
        } else if (fromDate != null) {
            baseCriteria.and("createdAt").gte(fromDate);
        } else if (toDate != null) {
            baseCriteria.and("createdAt").lte(toDate);
        }

        Map<String, Long> statusCounts = new HashMap<>();
        List<String> statuses = List.of(
                ReportStatus.FAILED.name(),
                ReportStatus.SUCCESS.name(),
                ReportStatus.PENDING.name()
        );

        for (String messageStatus : statuses) {
            Criteria statusCriteria = new Criteria().andOperator(
                    baseCriteria,
                    Criteria.where("status").is(messageStatus));
            Query query = new Query(statusCriteria);
            long count = mongoTemplate.count(query, Report.class);
            statusCounts.put(messageStatus.toLowerCase(), count);
        }

        return new ReportSummaryDto(
                statusCounts.getOrDefault("pending", 0L),
                statusCounts.getOrDefault("failed", 0L),
                statusCounts.getOrDefault("success", 0L));
    }

    public Page<ReportResponseDto> getFilteredReportsForAdmin(
            Long userId,
            Long broadcastId,
            Long campaignId,
            String tagLogId,
            String messageId,
            String status,
            String type,
            Instant fromDate,
            Instant toDate,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Criteria criteria = new Criteria();

        if (userId != null) {
            criteria.and("userId").is(userId);
        }
        if (broadcastId != null) {
            criteria.and("broadcastId").is(broadcastId);
        }
        if (campaignId != null) {
            criteria.and("campaignId").is(campaignId);
        }
        if (tagLogId != null) {
            criteria.and("tagLogId").is(tagLogId);
        }
        if (messageId != null) {
            criteria.and("messageId").is(messageId);
        }
        if (status != null) {
            criteria.and("status").is(status);
        }
        if (type != null) {
            criteria.and("type").is(type);
        }
        if (fromDate != null && toDate != null) {
            criteria.and("createdAt").gte(fromDate).lte(toDate);
        } else if (fromDate != null) {
            criteria.and("createdAt").gte(fromDate);
        } else if (toDate != null) {
            criteria.and("createdAt").lte(toDate);
        }

        Query query = new Query(criteria).with(pageable);
        List<Report> reports = mongoTemplate.find(query, Report.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Report.class);

        List<ReportResponseDto> dtos = reports.stream()
                .map(reportMapper::toReportDto)
                .toList();

        return new PageImpl<>(dtos, pageable, total);
    }

    public boolean softDeleteReportById(String reportId) {
        Query query = new Query(Criteria.where("id").is(reportId).and("deletedAt").is(null));
        Update update = new Update().set("deletedAt", Instant.now());

        UpdateResult result = mongoTemplate.updateFirst(query, update, Report.class);
        return result.getModifiedCount() > 0;
    }

    public boolean restoreReportById(String reportId) {
        Query query = new Query(Criteria.where("id").is(reportId).and("deletedAt").ne(null));
        Update update = new Update().unset("deletedAt");
        UpdateResult result = mongoTemplate.updateFirst(query, update, Report.class);
        return result.getModifiedCount() > 0;
    }

    public long bulkSoftDeleteByFilter(
            String userId,
            String campaignId,
            String broadcastId,
            String status,
            String type,
            Instant fromDate,
            Instant toDate) {
        Criteria criteria = new Criteria().and("deletedAt").is(null); // Only active ones

        if (userId != null)
            criteria.and("userId").is(userId);
        if (campaignId != null)
            criteria.and("campaignId").is(campaignId);
        if (broadcastId != null)
            criteria.and("broadcastId").is(broadcastId);
        if (status != null)
            criteria.and("status").is(status);
        if (type != null)
            criteria.and("type").is(type);
        if (fromDate != null && toDate != null) {
            criteria.and("createdAt").gte(fromDate).lte(toDate);
        } else if (fromDate != null) {
            criteria.and("createdAt").gte(fromDate);
        } else if (toDate != null) {
            criteria.and("createdAt").lte(toDate);
        }

        Query query = new Query(criteria);
        Update update = new Update().set("deletedAt", Instant.now());

        UpdateResult result = mongoTemplate.updateMulti(query, update, Report.class);
        return result.getModifiedCount();
    }

    public List<Report> getPendingRetryableReports(String status, boolean retryable) {
        Criteria criteria = new Criteria()
                .and("status").is(status)
                .and("retryable").is(retryable)
                .and("deletedAt").is(null); // exclude archived

        Query query = new Query(criteria).limit(100); // optional: cap batch size
        return mongoTemplate.find(query, Report.class);
    }

    public boolean updateReportStatusByMessageId(String messageId, String status, Instant updatedAt) {
        Query query = new Query(Criteria.where("messageId").is(messageId).and("deletedAt").is(null));
        Update update = new Update()
                .set("status", status)
                .set("updatedAt", updatedAt != null ? updatedAt : Instant.now());

        UpdateResult result = mongoTemplate.updateFirst(query, update, Report.class);
        return result.getModifiedCount() > 0;
    }

}
