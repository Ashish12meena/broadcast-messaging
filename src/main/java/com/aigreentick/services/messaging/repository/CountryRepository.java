package com.aigreentick.services.messaging.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country,Long>   {
    Optional<Country> findByName(String name);

    Optional<Country> findByMobileCodeAndDeletedAtIsNull(String code);

    Optional<Country> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Optional<Long> findIdByMobileCodeAndDeletedAtIsNull(String mobileCode);


}