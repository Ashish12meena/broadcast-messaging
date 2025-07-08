package com.aigreentick.services.messaging.service.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.aigreentick.services.messaging.enums.CampaignStatus;
import com.aigreentick.services.messaging.model.Campaign;

public interface CampaignInterface {
    
    Campaign createCampaign(Campaign campaign);

    Optional<Campaign> getCampaignById(Long id);

    List<Campaign> getCampaignsByUser(Long userId);

    List<Campaign> getPendingScheduledCampaigns(LocalDateTime now);

    void updateStatus(Long id, CampaignStatus status);

    void softDeleteCampaign(Long id);
}

