package com.sounganization.botanify.domain.chat;

import com.sounganization.botanify.common.config.ConnectionRecoveryScheduler;
import com.sounganization.botanify.common.config.websocket.handler.ConnectionFailureHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionRecoverySchedulerTest {

    @Mock
    private ConnectionFailureHandler connectionFailureHandler;

    @InjectMocks
    private ConnectionRecoveryScheduler connectionRecoveryScheduler;

    @Test
    @DisplayName("인터넷 연결 가능 시 메시지 복구 시도")
    void checkConnectionAndRecover_WhenInternetIsAvailable() {
        // Given
        try (MockedStatic<InetAddress> mockedInetAddress = mockStatic(InetAddress.class)) {
            InetAddress mockAddress = mock(InetAddress.class);
            when(mockAddress.isReachable(3000)).thenReturn(true);
            mockedInetAddress.when(() -> InetAddress.getByName("8.8.8.8"))
                    .thenReturn(mockAddress);

            // When
            connectionRecoveryScheduler.checkConnectionAndRecover();

            // Then
            verify(connectionFailureHandler).attemptMessageRecovery();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("인터넷 연결 불가능 시 메시지 복구 시도하지 않음")
    void checkConnectionAndRecover_WhenInternetIsNotAvailable() throws IOException {
        // Given
        try (MockedStatic<InetAddress> mockedInetAddress = mockStatic(InetAddress.class)) {
            InetAddress mockAddress = mock(InetAddress.class);
            when(mockAddress.isReachable(3000)).thenReturn(false);
            mockedInetAddress.when(() -> InetAddress.getByName("8.8.8.8"))
                    .thenReturn(mockAddress);

            // When
            connectionRecoveryScheduler.checkConnectionAndRecover();

            // Then
            verify(connectionFailureHandler, never()).attemptMessageRecovery();
        }
    }

    @Test
    @DisplayName("인터넷 연결 확인 중 예외 발생 시 처리")
    void checkConnectionAndRecover_WhenExceptionOccurs() {
        // Given
        try (MockedStatic<InetAddress> mockedInetAddress = mockStatic(InetAddress.class)) {
            mockedInetAddress.when(() -> InetAddress.getByName("8.8.8.8"))
                    .thenThrow(new UnknownHostException("연결 실패"));

            // When
            connectionRecoveryScheduler.checkConnectionAndRecover();

            // Then
            verify(connectionFailureHandler, never()).attemptMessageRecovery();
        }
    }
}
