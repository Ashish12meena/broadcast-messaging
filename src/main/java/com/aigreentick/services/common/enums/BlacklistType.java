package com.aigreentick.services.common.enums;

public enum BlacklistType {
    BLOCKED_BY_ADMIN,      // Admin/system blocks this user
    BLOCKED_BY_USER        // User blocked the sender (opt-out, e.g., STOP message)
}

