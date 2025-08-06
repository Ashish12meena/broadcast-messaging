package com.aigreentick.services.template.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.aigreentick.services.template.enums.TemplateStatus;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "templates")
public class Template {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("category")
    private String category;

    @Field("language")
    private String language;

    @Field("status")
    private TemplateStatus status;

    @Field("rejection_reason")
    private String rejectionReason;

    @Field("previous_category")
    private String previousCategory;

    @Field("meta_template_id")
    private String metaTemplateId;

    @Field("whatsapp_id")
    private String whatsappId;

    @Field("submission_payload")
    private String submissionPayload;

    @Field("components")
    private List<TemplateComponent> components;

    @Field("response")
    private JsonNode response;

    @Field("components_json")
    private JsonNode componentsJson;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("deleted_at")
    private LocalDateTime deletedAt;

    @Field("user_id")
    @Indexed(name = "idx_templates_user_id")
    private String userId;
}
