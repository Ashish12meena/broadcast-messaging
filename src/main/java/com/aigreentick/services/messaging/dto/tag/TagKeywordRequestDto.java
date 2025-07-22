package com.aigreentick.services.messaging.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TagKeywordRequestDto {
    @NotNull
    private Long tagId;

    @NotBlank
    private String name;
}
