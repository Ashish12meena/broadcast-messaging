package com.aigreentick.services.messaging.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aigreentick.services.messaging.enums.CampaignStatus;
import com.aigreentick.services.messaging.model.Campaign;
import com.aigreentick.services.messaging.repository.CampaignRepository;
import com.aigreentick.services.messaging.service.interfaces.CampaignInterface;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CampaignServiceImpl implements CampaignInterface {

    private final CampaignRepository campaignRepository;

    public CampaignServiceImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public Optional<Campaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    @Override
    public List<Campaign> getCampaignsByUser(Long userId) {
        return campaignRepository.findByUserId(userId);
    }

    @Override
    public List<Campaign> getPendingScheduledCampaigns(LocalDateTime now) {
        return campaignRepository.findScheduledCampaignsToRun(CampaignStatus.SCHEDULED, now);
    }

    @Override
    public void updateStatus(Long id, CampaignStatus status) {
        int updated = campaignRepository.updateCampaignStatusById(id, status);
        if (updated == 0) {
            throw new EntityNotFoundException("Campaign not found with id: " + id);
        }
    }

    @Override
    public void softDeleteCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id).orElseThrow();
        campaign.setDeletedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
    }
}

