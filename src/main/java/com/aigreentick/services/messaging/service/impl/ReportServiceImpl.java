package com.aigreentick.services.messaging.service.impl;

import java.time.Instant;
import java.util.ArrayList;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.aigreentick.services.common.constants.CommonConstants;
import com.aigreentick.services.messaging.constants.MessagingConstants;
import com.aigreentick.services.messaging.dto.ReportResponseDto;
import com.aigreentick.services.messaging.mapper.ReportMapper;
import com.aigreentick.services.messaging.model.Report;
import com.aigreentick.services.messaging.repository.ReportRepository;
import com.aigreentick.services.messaging.service.interfaces.ReportInterface;
import com.aigreentick.services.whatsapp.service.WhatsappServiceImpl;

@Service
public class ReportServiceImpl implements ReportInterface {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final WhatsappServiceImpl whatsappService;
    private final MongoTemplate mongoTemplate;
    private final ExecutorService whatsappExecutor;
    private final Semaphore semaphore;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportServiceImpl(WhatsappServiceImpl whatsappService, MongoTemplate mongoTemplate,
            @Qualifier("whatsappExecutor") ExecutorService whatsappExecutor,
            Semaphore whatsappConcurrencySemaphore,
            ReportRepository reportRepository,
            ReportMapper reportMapper) {
        this.whatsappService = whatsappService;
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
            });
        }

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

    @Override
    public Page<ReportResponseDto> getReportsByBroadcastId(Long broadCastId, Pageable pageable ) {
        Page<Report> reports = reportRepository.findByBroadcastId(broadCastId,pageable);
        return reports.map(reportMapper::toReportDto);
    }

    private Report processReport(Report report, String template) {
        try {
            Map<String, Object> response = whatsappService.sendMessage(report.getMobile(), template);
            report.setMessageStatus((String) response.get("status"));
            report.setMessageId((String) response.get("messageId"));
            report.setWaId((String) response.get("waId"));
            report.setStatus(CommonConstants.STATUS_DELIVERED.equalsIgnoreCase(report.getMessageStatus())
                    ? CommonConstants.STATUS_SUCCESS
                    : CommonConstants.STATUS_FAILED);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to {}", report.getMobile(), e);
            report.setStatus(CommonConstants.STATUS_FAILED);
            report.setMessageStatus(CommonConstants.STATUS_ERROR);
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

}

