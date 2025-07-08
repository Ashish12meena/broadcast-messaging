package com.aigreentick.services.common.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CommonUtil {
     private CommonUtil() {
        // Prevent instantiation
    }

    public static String generateApiKey() {
        byte[] bytes = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Generates a 48-character random key using two UUIDs.
     * 
     * @return A 48-character hexadecimal key
     */
    public static String generateRandomKey() {
        return UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static long generateRandomLongId() {
        return ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
    }

}
