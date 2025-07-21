package com.aigreentick.services.messaging.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.tag.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByUserIdAndName(Long userId, String tagName);

    boolean existsByUserIdAndName(Long userId, String tagName);

    List<Tag> findAll(Specification<Tag> spec);

    Optional<Tag> findByIdAndUserIdAndDeletedAtIsNull(Long tagId, Long userId);

    @Query("SELECT DISTINCT t FROM Tag t JOIN t.keywords k " +
            "WHERE t.user.id = :userId AND LOWER(k.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND t.deletedAt IS NULL")
    List<Tag> findTagsByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Query("SELECT DISTINCT t FROM Tag t JOIN t.numbers n " +
            "WHERE t.user.id = :userId AND n.number LIKE CONCAT('%', :phoneNumber, '%') " +
            "AND t.deletedAt IS NULL")
    List<Tag> findTagsByNumber(@Param("userId") Long userId, @Param("phoneNumber") String phoneNumber);

    List<Tag> findAllByDeletedAtBefore(LocalDateTime threshold);

    long countByUserIdAndDeletedAtIsNull(Long userId);

}
