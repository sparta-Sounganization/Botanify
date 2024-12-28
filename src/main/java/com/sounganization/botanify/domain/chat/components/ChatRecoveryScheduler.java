package com.sounganization.botanify.domain.chat.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRecoveryScheduler {
    private final ChatFailureHandler chatFailureHandler;

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void recoverUndeliveredMessages() {
        chatFailureHandler.handleMessageRecovery();
    }
}
