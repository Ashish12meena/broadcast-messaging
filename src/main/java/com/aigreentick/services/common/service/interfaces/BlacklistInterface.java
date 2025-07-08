package com.aigreentick.services.common.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aigreentick.services.common.dto.BlacklistRequestDto;
import com.aigreentick.services.common.dto.BlacklistResponseDto;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.common.enums.BlacklistType;

public interface BlacklistInterface {
      List<String> filterNonBlacklistedNumbers(List<String> mobiles, Long countryId, Long senderId);

    ResponseMessage<String> addToBlacklist(BlacklistRequestDto blacklistRequestDto, Long senderId);

    ResponseMessage<String> addToBlacklistByAdmin(BlacklistRequestDto blacklistRequestDto);

    Page<BlacklistResponseDto> getFilteredBlacklistEntries(
            String mobile,
            Long countryId,
            BlacklistType type,
            Boolean isBlocked,
            Boolean isExpired,
            Pageable pageable);

}
