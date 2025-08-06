package com.aigreentick.services.template.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateComponent {

    @Field("type")
    private String type;

    @Field("format")
    private String format;

    @Field("text")
    private String text;

    @Field("image_url")
    private String imageUrl;

    @Field("components_buttons")
    private List<TemplateComponentButton> componentsButtons;

     @DBRef
    private List<TemplateText> texts;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("deleted_at")
    private LocalDateTime deletedAt;
}
