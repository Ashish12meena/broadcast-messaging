package com.aigreentick.services.messaging.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.model.Template;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    Page<Template> findAll(Pageable pageable);
    

    Page<Template>findByUser(User user,Pageable pageable);

    Page<Template> findByUserAndNameContainingIgnoreCase(User user, String search, Pageable pageable);
}
