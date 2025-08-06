package com.aigreentick.services.template.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.template.enums.TemplateStatus;
import com.aigreentick.services.template.model.Template;
// import com.aigreentick.services.template.dto.CategoryCountDto;
// import com.aigreentick.services.template.dto.StatusCountDto;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {

    Page<Template> findAll(Pageable pageable);

    Page<Template> findByUserIdAndNameContainingIgnoreCase(Long userId, String search, Pageable pageable);

    Optional<Template> findByIdAndUserId(String id, Long userId);

    List<Template> findByUserId(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, TemplateStatus status);

    long countByStatus(TemplateStatus status);

    List<Template> findByNameContainingIgnoreCaseOrWhatsappIdContainingIgnoreCase(String name, String waId);

    long countByDeletedAtIsNull();

    List<Template> findByStatus(String status);

    Optional<Template> findByName(String name);

    Page<Template> findByUserIdAndStatusAndNameContainingIgnoreCase(Long userId, TemplateStatus statusEnum, String search, Pageable pageable);

    Page<Template> findByUserIdAndStatus(Long userId, TemplateStatus statusEnum, Pageable pageable);

    // @Aggregation(pipeline = {
    //     "{ $match: { deletedAt: null } }",
    //     "{ $group: { _id: '$status', count: { $sum: 1 } } }",
    //     "{ $project: { status: '$_id', count: 1, _id: 0 } }"
    // })
    // List<StatusCountDto> countByStatusGrouped();

    // @Aggregation(pipeline = {
    //     "{ $match: { deletedAt: null } }",
    //     "{ $group: { _id: '$category', count: { $sum: 1 } } }",
    //     "{ $project: { category: '$_id', count: 1, _id: 0 } }"
    // })
    // List<CategoryCountDto> countByCategoryGrouped();
}
