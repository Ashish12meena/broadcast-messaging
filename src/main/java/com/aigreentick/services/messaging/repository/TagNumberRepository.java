package com.aigreentick.services.messaging.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.tag.TagNumber;

@Repository
public interface TagNumberRepository extends JpaRepository<TagNumber, Long> {

    List<TagNumber> findByTagId(Long tagId);

    @Modifying
    @Query("DELETE FROM TagNumber tn WHERE tn.number = :number AND tn.tag.user.id = :userId")
    void deleteByNumberAndUserId(@Param("number") String number, @Param("userId") Long userId);

}
