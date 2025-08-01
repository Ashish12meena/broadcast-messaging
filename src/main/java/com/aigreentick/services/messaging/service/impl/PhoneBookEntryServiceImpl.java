package com.aigreentick.services.messaging.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.common.exception.ArgumentCannotBeNullOrBlankException;
import com.aigreentick.services.messaging.dto.phonebook.PhoneBookEntryRequestDto;
import com.aigreentick.services.messaging.dto.phonebook.PhoneBookResponseDto;
import com.aigreentick.services.messaging.mapper.PhoneBookEntryMapper;
import com.aigreentick.services.messaging.model.PhoneBookEntry;
import com.aigreentick.services.messaging.repository.PhoneBookEntryRepository;
import com.aigreentick.services.messaging.util.PhoneNumberUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhoneBookEntryServiceImpl {
    private final PhoneBookEntryRepository phoneBookEntryRepository;
    private final PhoneBookEntryMapper phoneBookEntryMapper;
    private final UserService userService;

    public PhoneBookResponseDto createPhoneBookEntry(PhoneBookEntryRequestDto dto, long userId) {
        String phoneNumber = PhoneNumberUtils.buildWhatsAppNumber(dto.getCountryCode(), dto.getPhoneNumber());
        boolean exists = phoneBookEntryRepository.existsByPhoneNumberAndUserId(phoneNumber, userId);
        if (exists) {
            throw new IllegalStateException("PhoneBookEntry with this number already exists for this user.");
        }
        User user = userService.getReferenceById(userId);
        PhoneBookEntry entry = phoneBookEntryMapper.toEntity(dto, phoneNumber, user);
         phoneBookEntryRepository.save(entry);
         return phoneBookEntryMapper.toDto(entry);
         
    }

    public void addParameter(String key, String value, long entryId) {
        PhoneBookEntry phoneBookEntry = findById(entryId);
        Map<String, String> parameters = phoneBookEntry.getParametersJson();
        parameters.put(key, value);
        phoneBookEntryRepository.save(phoneBookEntry);
    }

    public List<PhoneBookEntry> getEntriesByUserId(Long userId) {
        if (userId == null) {
            throw new ArgumentCannotBeNullOrBlankException("User not cannot be null or blank " + userId);
        }

        boolean userExists = userService.existsById(userId);
        if (!userExists) {
            throw new UserNotFoundException("User with ID " + userId + " does not exist.");
        }

        return phoneBookEntryRepository.findAllByUserId(userId);
    }

    public List<PhoneBookEntry> getListOfPhoneBookEntriesByUserIdAndPhoneNumbers(long userId,
            List<String> phoneNumbers) {
        return phoneBookEntryRepository.findByUserIdAndPhoneNumberIn(userId, phoneNumbers)
                .orElse(Collections.emptyList());
    }

    public String getParameterValueByUserIdAndPhoneNumber(Long userId, String phoneNumber, String key) {
        String defaultValue = "variable";
        if (userId == null) {
            throw new ArgumentCannotBeNullOrBlankException("UserId ID must not be null.");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new ArgumentCannotBeNullOrBlankException("Phone number must not be null or blank.");
        }

        if (key == null || key.isBlank()) {
            throw new ArgumentCannotBeNullOrBlankException("Parameter key must not be null or blank.");
        }

        PhoneBookEntry entry = phoneBookEntryRepository
                .findByUserIdAndPhoneNumber(userId, phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No PhoneBookEntry found for userId =" + userId + " and phoneNumber=" + phoneNumber));

        Map<String, String> parameters = entry.getParametersJson();
        return (parameters != null && parameters.containsKey(key)) ? parameters.get(key) : defaultValue;
    }

    /**
     * Returns all parameters for the given entry ID.
     */
    public Map<String, String> getParametersByEntryId(Long entryId) {
        PhoneBookEntry entry = phoneBookEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entry not found with ID: " + entryId));
        return entry.getParametersJson();
    }

    /**
     * Returns a specific parameter value by key.
     */
    public String getParameterByKey(Long entryId, String key) {
        PhoneBookEntry entry = phoneBookEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entry not found with ID: " + entryId));
        return entry.getParametersJson().get(key);
    }

    /**
     * Updates the parameters (replaces entire map).
     */
    public PhoneBookEntry updateParameters(Long entryId, Map<String, String> newParams) {
        PhoneBookEntry entry = phoneBookEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entry not found with ID: " + entryId));

        entry.setParametersJson(newParams);
        return phoneBookEntryRepository.save(entry);
    }

    /**
     * Updates a single parameter by key (adds or updates).
     */
    public PhoneBookEntry updateParameterByKey(Long entryId, String key, String value) {
        PhoneBookEntry entry = phoneBookEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entry not found with ID: " + entryId));

        Map<String, String> parameters = entry.getParametersJson();
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
        entry.setParametersJson(parameters);

        return phoneBookEntryRepository.save(entry);
    }

    /**
     * Deletes a parameter by key.
     */
    public PhoneBookEntry removeParameterByKey(Long entryId, String key) {
        PhoneBookEntry entry = phoneBookEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entry not found with ID: " + entryId));

        Map<String, String> parameters = entry.getParametersJson();
        if (parameters != null && parameters.containsKey(key)) {
            parameters.remove(key);
            entry.setParametersJson(parameters);
            return phoneBookEntryRepository.save(entry);
        }

        return entry;
    }


    public PhoneBookEntry findById(Long entryId) {
        return phoneBookEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("phone book entry not found by id " + entryId));
    }
}
