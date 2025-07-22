package com.aigreentick.services.messaging.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.tag.TagKeyword;


@Repository
public interface TagKeywordRepository extends JpaRepository<TagKeyword,Long> {

    Optional<TagKeyword> findByTagIdAndDeletedAtIsNull(Long tagId);
    
}
