package com.aigreentick.services.messaging.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.PhoneBookEntry;

@Repository
public interface PhoneBookEntryRepository extends JpaRepository<PhoneBookEntry,Long> {

    boolean existsByPhoneNumberAndTemplateId(String phoneNumber, long templateId);

    List<PhoneBookEntry> findAllByTemplateId(Long templateId);

    Optional<PhoneBookEntry> findByTemplateIdAndPhoneNumber(Long templateId, String phoneNumber);
    
}
