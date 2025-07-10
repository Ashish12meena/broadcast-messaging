package com.aigreentick.services.messaging.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.enums.TemplateStatus;
import com.aigreentick.services.messaging.model.Template;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    Page<Template> findAll(Pageable pageable);

    Page<Template> findByUser(User user, Pageable pageable);

    Page<Template> findByUserAndNameContainingIgnoreCase(User user, String search, Pageable pageable);

    Optional<Template> findByIdAndUserId(Long id, Long userId);

    List<Template> findByUserId(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, TemplateStatus status);

    long countByStatus(TemplateStatus status);

    List<Template> findByNameContainingIgnoreCaseOrWaIdContainingIgnoreCase(String name, String waId);

    Page<Template> findAll(Specification<Template> spec, Pageable pageable);

    @Query("SELECT t.status, COUNT(t) FROM Template t WHERE t.deletedAt IS NULL GROUP BY t.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT t.category, COUNT(t) FROM Template t WHERE t.deletedAt IS NULL GROUP BY t.category")
    List<Object[]> countByCategoryGrouped();

    long countByDeletedAtIsNull();
}
