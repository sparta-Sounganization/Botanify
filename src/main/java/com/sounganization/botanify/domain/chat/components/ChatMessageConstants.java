package com.sounganization.botanify.domain.chat.components;

import org.springframework.stereotype.Component;

@Component
public class ChatMessageConstants {
    public static final int DEFAULT_RETENTION_DAYS = 90;
    public static final int BATCH_SIZE = 1000;
    public static final String CLEANUP_CRON = "0 0 2 * * *";  // 매일 새벽 2시
}
