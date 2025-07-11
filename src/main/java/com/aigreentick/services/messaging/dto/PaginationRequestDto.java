package com.aigreentick.services.messaging.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PaginationRequestDto {

    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 10;
}