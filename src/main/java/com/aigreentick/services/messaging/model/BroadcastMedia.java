package com.aigreentick.services.messaging.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Document(collection = "broadcast_media")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMedia {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name="broadcast_id")
    private Broadcast broadcast;

    @Field("type")
    private String type;

    @Field("url")
    private String url;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("deleted_at")
    private Instant deletedAt;
}

