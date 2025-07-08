package com.aigreentick.services.common.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aigreentick.services.common.constants.CommonConstants;
import com.aigreentick.services.common.dto.BlacklistRequestDto;
import com.aigreentick.services.common.dto.BlacklistResponseDto;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.common.enums.BlacklistType;
import com.aigreentick.services.common.mapper.BlacklistMapper;
import com.aigreentick.services.common.model.Blacklist;
import com.aigreentick.services.common.repository.BlacklistRepository;
import com.aigreentick.services.common.service.interfaces.BlacklistInterface;
import com.aigreentick.services.messaging.service.impl.CountryServiceImpl;

@Service
public class BlacklistServiceImpl implements BlacklistInterface {
    Logger log = LoggerFactory.getLogger(BlacklistServiceImpl.class);
    private final BlacklistRepository blacklistRepository;
    private final BlacklistMapper blacklistMapper;
    private final CountryServiceImpl countryService;
    

    public BlacklistServiceImpl(BlacklistRepository blacklistRepository, BlacklistMapper blacklistMapper,CountryServiceImpl countryService) {
        this.blacklistRepository = blacklistRepository;
        this.blacklistMapper = blacklistMapper;
        this.countryService = countryService;
    }

    @Override
    public List<String> filterNonBlacklistedNumbers(List<String> mobiles, Long countryId, Long senderId) {
        if (mobiles == null || mobiles.isEmpty())
            return Collections.emptyList();

        List<Blacklist> blacklisted = blacklistRepository.findBlacklistedNumbers(mobiles, countryId, senderId);

        Set<String> blacklistedSet = blacklisted.stream()
                .map(Blacklist::getMobile)
                .collect(Collectors.toSet());

        return mobiles.stream()
                .filter(m -> !blacklistedSet.contains(m))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseMessage<String> addToBlacklist(BlacklistRequestDto blacklistRequestDto, Long senderId) {
        if (isAlreadyBlacklistedByUser(blacklistRequestDto.getMobile(), senderId)) {
            return new ResponseMessage<>("failed", "Already blacklisted", null);
        }

        Blacklist blacklist = blacklistMapper.toBlacklistEntity(blacklistRequestDto);
        blacklist.setSenderId(senderId);
        blacklist.setType(BlacklistType.BLOCKED_BY_USER);
        blacklistRepository.save(blacklist);
        return new ResponseMessage<>("success", "User blacklisted sucesfully", null);
    }

    @Override
    public ResponseMessage<String> addToBlacklistByAdmin(BlacklistRequestDto blacklistRequestDto) {
        Long countryId = blacklistRequestDto.getCountryId();

        // Dynamically resolve Global ID if not provided
        if (countryId == null) {
            countryId = countryService.getCountryIdByMobileCode(CommonConstants.GLOBAL_COUNTRY_CODE);
        }
        if (isAlreadyBlacklistedByAdmin(blacklistRequestDto.getMobile(), countryId)) {
            return new ResponseMessage<>("failed", "User blacklisted succesfully by admin", null);
        }
        Blacklist blacklist = blacklistMapper.toBlacklistEntity(blacklistRequestDto);
        blacklist.setType(BlacklistType.BLOCKED_BY_ADMIN);
        blacklistRepository.save(blacklist);
        return new ResponseMessage<>("success", "User blacklisted successfully by admin", null);
    }

    @Override
    public Page<BlacklistResponseDto> getFilteredBlacklistEntries(String mobile, Long countryId, BlacklistType type,
            Boolean isBlocked, Boolean isExpired, Pageable pageable) {

        Page<Blacklist> page = blacklistRepository.findFilteredBlacklistEntries(
                mobile, countryId, type, isBlocked, isExpired, pageable);
        return page.map(blacklistMapper::toBlacklistResponseDto);
    }

    public boolean isAlreadyBlacklistedByAdmin(String mobile, Long countryId) {
        return blacklistRepository.existsByAdmin(mobile, countryId, BlacklistType.BLOCKED_BY_ADMIN);
    }

    public boolean isAlreadyBlacklistedByUser(String mobile, Long senderId) {
         return blacklistRepository.existsByUser(mobile, BlacklistType.BLOCKED_BY_USER, senderId);
    }

}
