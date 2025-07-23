package com.aigreentick.services.messaging.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.aigreentick.services.messaging.dto.phonebook.PhoneBookEntryRequestDto;
import com.aigreentick.services.messaging.mapper.PhoneBookEntryMapper;
import com.aigreentick.services.messaging.model.PhoneBookEntry;
import com.aigreentick.services.messaging.repository.PhoneBookEntryRepository;
import com.aigreentick.services.messaging.util.PhoneNumberUtils;
import com.aigreentick.services.template.model.Template;
import com.aigreentick.services.template.service.impl.TemplateServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhoneBookEntryServiceImpl {
    private final TemplateServiceImpl templateService;
    private final PhoneBookEntryRepository phoneBookEntryRepository;
    private final PhoneBookEntryMapper phoneBookEntryMapper;

    public PhoneBookEntry createPhoneBookEntry(PhoneBookEntryRequestDto dto) {
        String phoneNumber = PhoneNumberUtils.buildWhatsAppNumber(dto.getCountryCode(), dto.getPhoneNumber());
        Template template = templateService.findById(dto.getTemplateId());
        boolean exists = phoneBookEntryRepository.existsByPhoneNumberAndTemplateId(phoneNumber, dto.getTemplateId());
        if (exists) {
            throw new IllegalStateException("PhoneBookEntry with this number already exists for the template.");
        }
        PhoneBookEntry entry = phoneBookEntryMapper.toEntity(dto, template);
        return phoneBookEntryRepository.save(entry);
    }

    public List<PhoneBookEntry> getEntriesByTemplateId(Long templateId) {
        if (templateId == null) {
            throw new IllegalArgumentException("Template ID must not be null.");
        }

        boolean templateExists = templateService.existsById(templateId);
        if (!templateExists) {
            throw new EntityNotFoundException("Template with ID " + templateId + " does not exist.");
        }

        return phoneBookEntryRepository.findAllByTemplateId(templateId);
    }

    public String getParameterValueByTemplateIdAndPhoneNumber(Long templateId, String phoneNumber, String key) {
        String defaultValue = "variable";
        if (templateId == null) {
            throw new IllegalArgumentException("Template ID must not be null.");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be null or blank.");
        }

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Parameter key must not be null or blank.");
        }

        PhoneBookEntry entry = phoneBookEntryRepository
                .findByTemplateIdAndPhoneNumber(templateId, phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No PhoneBookEntry found for templateId=" + templateId + " and phoneNumber=" + phoneNumber));

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

    public ArrayNode buildParameterArray(Map<String, String> parameters, ObjectMapper mapper) {
        ArrayNode parameterArray = mapper.createArrayNode();

        parameters.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())))
                .forEach(entry -> {
                    ObjectNode textNode = mapper.createObjectNode();
                    textNode.put("type", "text");
                    textNode.put("text", entry.getValue());

                    ObjectNode paramNode = mapper.createObjectNode();
                    paramNode.set("text", textNode.get("text")); // Only add raw text

                    parameterArray.add(paramNode);
                });

        return parameterArray;
    }

    public int extractPlaceholderCount(String text) {
        Matcher matcher = Pattern.compile("\\{\\{(\\d+)}}").matcher(text);
        Set<Integer> placeholders = new HashSet<>();
        while (matcher.find()) {
            placeholders.add(Integer.parseInt(matcher.group(1)));
        }
        return placeholders.size();
    }

}
