package com.aigreentick.services.messaging.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.PhoneBookEntry;

@Repository
public interface PhoneBookEntryRepository extends JpaRepository<PhoneBookEntry,Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndUserId(String phoneNumber, long userId);

    List<PhoneBookEntry> findAllByUserId(Long userId);

    Optional<List<PhoneBookEntry>> findByUserIdAndPhoneNumberIn(long userId, List<String> phoneNumbers);

    Optional<PhoneBookEntry> findByUserIdAndPhoneNumber(Long userId, String phoneNumber);
}
