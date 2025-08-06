package com.aigreentick.services.template.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateComponentButton {

    @Field("type")
    private String type; // QUICK_REPLY, URL, PHONE_NUMBER, OTP

    @Field("number")
    private String number;

    @Field("text")
    private String text;

    @Field("url")
    private String url;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("deleted_at")
    private LocalDateTime deletedAt;
}
