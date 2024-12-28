package com.sounganization.botanify.common.config;

import com.sounganization.botanify.common.config.websocket.handler.ConnectionFailureHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConnectionRecoveryScheduler {
    private final ConnectionFailureHandler connectionFailureHandler;

    @Scheduled(fixedDelay = 30000) // 30초마다 확인
    public void checkConnectionAndRecover() {
        if (isInternetAvailable()) {
            connectionFailureHandler.attemptMessageRecovery();
        }
    }

    private boolean isInternetAvailable() {
        try {
            return InetAddress.getByName("8.8.8.8").isReachable(3000);
        } catch (IOException e) {
            log.error("인터넷 연결 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
}
