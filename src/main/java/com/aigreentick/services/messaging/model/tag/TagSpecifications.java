package com.aigreentick.services.messaging.model.tag;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class TagSpecifications{

    public static Specification<Tag> userIdEquals(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Tag> nameOrKeywordLike(String search) {
        return (root, query, cb) -> {
            Join<Tag, TagKeyword> keywords = root.join("keywords", JoinType.LEFT);
            String pattern = "%" + search.toLowerCase() + "%";
            Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
            Predicate keywordLike = cb.like(cb.lower(keywords.get("name")), pattern);
            return cb.or(nameLike, keywordLike);
        };
    }

    public static Specification<Tag> createdAtAfter(LocalDate from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay());
    }

    public static Specification<Tag> createdAtBefore(LocalDate to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to.atTime(LocalTime.MAX));
    }
}
