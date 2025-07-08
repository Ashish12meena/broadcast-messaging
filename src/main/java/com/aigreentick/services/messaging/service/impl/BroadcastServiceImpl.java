package com.aigreentick.services.messaging.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.repository.UserRepository;
import com.aigreentick.services.common.constants.CommonConstants;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.common.service.impl.BlacklistServiceImpl;
import com.aigreentick.services.messaging.constants.MessagingConstants;
import com.aigreentick.services.messaging.dto.BroadcastRequestDTO;
import com.aigreentick.services.messaging.enums.BroadcastStatus;
import com.aigreentick.services.messaging.model.Broadcast;
import com.aigreentick.services.messaging.model.BroadcastMedia;
import com.aigreentick.services.messaging.model.Country;
import com.aigreentick.services.messaging.model.Report;
import com.aigreentick.services.messaging.model.Template;
import com.aigreentick.services.messaging.repository.BroadCastMediaRepository;
import com.aigreentick.services.messaging.repository.BroadCastRepository;
import com.aigreentick.services.messaging.repository.CountryRepository;
import com.aigreentick.services.messaging.repository.TemplateRepository;
import com.aigreentick.services.messaging.service.interfaces.BroadcastInterface;
import com.aigreentick.services.messaging.util.MessagingUtil;

/**
 * Creates and dispatches a broadcast.
 * Generates reports, saves them to DB, and initiates message sending.
 */
@Service
public class BroadcastServiceImpl implements BroadcastInterface {

    private static final Logger log = LoggerFactory.getLogger(BroadcastServiceImpl.class);
    private final ReportServiceImpl reportService;
    private final ExecutorService broadcastExecutor;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final TemplateRepository templateRepository;
    private final BroadCastRepository broadCastRepository;
    private final BroadCastMediaRepository broadCastMediaRepository;
    private final BlacklistServiceImpl blacklistService;

    public BroadcastServiceImpl(ReportServiceImpl reportService,
            @Qualifier("broadcastExecutor") ExecutorService broadcastExecutor,
            UserRepository userRepository,
            CountryRepository countryRepository,
            TemplateRepository templateRepository,
            BroadCastRepository broadCastRepository,
            BroadCastMediaRepository broadCastMediaRepository,
            BlacklistServiceImpl blacklistService) {

        this.reportService = reportService;
        this.broadcastExecutor = broadcastExecutor;
        this.userRepository = userRepository;
        this.countryRepository = countryRepository;
        this.templateRepository = templateRepository;
        this.broadCastRepository = broadCastRepository;
        this.broadCastMediaRepository = broadCastMediaRepository;
        this.blacklistService = blacklistService;
    }

    /**
     * Dispatches a broadcast by creating pending reports, saving them to the DB,
     * and then sending messages with controlled concurrency.
     */
    @Override
    public ResponseMessage<String> dispatch(BroadcastRequestDTO dto, Long userId) {
        long start = System.currentTimeMillis();

        CompletableFuture<List<String>> filteredMobileNumbersFuture = CompletableFuture
                .supplyAsync(() -> blacklistService.filterNonBlacklistedNumbers(dto.getMobileNumbers(),
                        dto.getCountryId(), userId));

        CompletableFuture<User> userFuture = CompletableFuture
                .supplyAsync(() -> userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found")));

        CompletableFuture<Template> templateFuture = CompletableFuture
                .supplyAsync(() -> templateRepository.findById(dto.getTemplateId())
                        .orElseThrow(() -> new RuntimeException("Template not found")));

        CompletableFuture<Country> countryFuture = CompletableFuture
                .supplyAsync(() -> countryRepository.findByName(dto.getCountry())
                        .orElseThrow(() -> new RuntimeException("Country not found")));

        // WhatsappAccount config =
        // whatsappAccountRepository.findByUserIdAndStatus(user.getId(), true)
        // .orElseThrow(() -> new RuntimeException("Active WhatsApp config not found"));

        // Get results
        User user = userFuture.join();
        Template template = templateFuture.join();
        Country country = countryFuture.join();
        List<String> filteredMobileNumbers = filteredMobileNumbersFuture.join();

        Broadcast broadcast = createAndSaveBroadcast(dto, filteredMobileNumbers, user, country, template);

        Long campaignId = null;
        Long broadcastId = broadcast.getId();

        List<Report> reports = buildPendingReports(dto, filteredMobileNumbers, campaignId, broadcastId, user);
        List<List<Report>> chunkedReports = MessagingUtil.chunkList(reports, MessagingConstants.BATCH_SIZE);

        saveAndProcessReports(chunkedReports, dto.getTemplate());

        long duration = System.currentTimeMillis() - start;
        log.info("Broadcast completed for {} numbers in {} ms", reports.size(), duration);

        return new ResponseMessage<>("Success", "BroadCast Initiated successfully", null);
    }

    private List<Report> buildPendingReports(BroadcastRequestDTO dto, List<String> filteredMobileNumbers,
            Long campaignId, Long broadcastId, User user) {
        Instant now = Instant.now();
        return filteredMobileNumbers.stream().map(mobile -> {
            Report report = new Report();
            report.setCampaignId(campaignId);
            report.setBroadcastId(broadcastId);
            report.setUserId(user.getId());
            report.setMobile(mobile);
            report.setStatus(CommonConstants.STATUS_PENDING);
            report.setMessageStatus(CommonConstants.STATUS_PENDING);
            report.setCreatedAt(now);
            report.setUpdatedAt(now);
            return report;
        }).collect(Collectors.toList());
    }

    /**
     * Creates a broadcast and saves it to DB. Throws if schedule date format is
     * wrong.
     */
    @Transactional
    private Broadcast createAndSaveBroadcast(BroadcastRequestDTO dto, List<String> filteredMobileNumbers, User user,
            Country country, Template template) {
        Broadcast broadcast = new Broadcast();
        broadcast.setUser(user);
        broadcast.setTemplate(template);
        broadcast.setCountry(country);
        broadcast.setCampanyName(dto.getCampName());
        broadcast.setMedia(dto.isMedia());
        broadcast.setTotalSent(dto.getMobileNumbers().size());
        broadcast.setRecipients(String.join(",", filteredMobileNumbers));
        broadcast.setStatus(BroadcastStatus.SENDING);
        if (dto.getScheduleDate() != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                LocalDateTime schedule = LocalDateTime.parse(dto.getScheduleDate(), formatter);
                broadcast.setScheduleAt(schedule);
                broadcast.setStatus(BroadcastStatus.SCHEDULED);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format. Expected: dd.MM.yyyy HH:mm");
            }
        }
        broadCastRepository.save(broadcast);
        if (dto.isMedia()) {
            saveMedia(dto, broadcast);
        }
        return broadcast;
    }

    /**
     * Saves broadcast media associated with a broadcast.
     */
    private void saveMedia(BroadcastRequestDTO dto, Broadcast broadcast) {
        BroadcastMedia media = new BroadcastMedia();
        media.setBroadcast(broadcast);
        media.setUrl(dto.getMediaUrl());
        media.setType(dto.getMediaType() != null ? dto.getMediaType() : "image");
        broadCastMediaRepository.save(media);
    }

    /**
     * Asynchronously saves and process report chunks.
     */
    private void saveAndProcessReports(List<List<Report>> chunkedReports, String templateName) {
        broadcastExecutor.submit(() -> {
            try {
                long begin = System.currentTimeMillis();

                reportService.saveReportChunks(chunkedReports);

                log.info("Total time {} taken to save {} chunks of reports",
                        (System.currentTimeMillis() - begin), chunkedReports.size());

                for (List<Report> processChunk : chunkedReports) {
                    reportService.sendAndUpdateReportsInControlledBatches(processChunk, templateName);
                }

                broadCastRepository.updateStatusById(BroadcastStatus.COMPLETED,
                        chunkedReports.get(0).get(0).getBroadcastId());

                log.info("Finished processing all report chunks time taken {} ", (System.currentTimeMillis() - begin));

            } catch (Exception e) {
                log.error("Error occurred during saving or processing reports", e);
            }
        });
    }

}

