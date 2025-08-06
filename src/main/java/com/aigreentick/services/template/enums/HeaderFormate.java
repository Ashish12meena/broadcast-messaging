package com.aigreentick.services.template.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HeaderFormate {
    TEXT("text"),
    IMAGE("image"),
    VIDEO("video"),
    DOCUMENT("document"),
    LOCATION("location");

    private final String value;

    HeaderFormate(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
