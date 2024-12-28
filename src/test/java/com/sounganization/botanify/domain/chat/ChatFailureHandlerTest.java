package com.sounganization.botanify.domain.chat;

import com.sounganization.botanify.common.config.websocket.service.WebSocketChatService;
import com.sounganization.botanify.domain.chat.components.ChatFailureHandler;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import com.sounganization.botanify.domain.chat.repository.ChatMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatFailureHandlerTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private WebSocketChatService webSocketChatService;

    @InjectMocks
    private ChatFailureHandler chatFailureHandler;

    @Test
    @DisplayName("Redis 장애 시 WebSocket service로 메시지 전달")
    void handleRedisFailure_ShouldDelegateToWebSocketService() {
        // Given
        ChatMessageReqDto message = new ChatMessageReqDto(
                ChatMessageReqDto.MessageType.TALK,
                1L,
                1L,
                "Test message"
        );

        // When
        chatFailureHandler.handleRedisFailure(message);

        // Then
        verify(webSocketChatService).handleChatMessage(message);
    }

    @Test
    @DisplayName("미전송 메시지 복구 처리 성공")
    void handleMessageRecovery_ShouldProcessUndeliveredMessages() {
        // Given
        ChatRoom chatRoom = ChatRoom.builder()
                .id(1L)
                .build();

        ChatMessage undeliveredMessage = ChatMessage.builder()
                .id(1L)
                .type(ChatMessage.MessageType.TALK)
                .senderId(1L)
                .content("메시지 전송 실패")
                .chatRoom(chatRoom)
                .delivered(false)
                .build();

        when(chatMessageRepository.findUndeliveredMessages())
                .thenReturn(List.of(undeliveredMessage));

        // When
        chatFailureHandler.handleMessageRecovery();

        // Then
        verify(webSocketChatService).handleChatMessage(any(ChatMessageReqDto.class));
        verify(chatMessageRepository).save(undeliveredMessage);
        assertTrue(undeliveredMessage.getDelivered());
    }

    @Test
    @DisplayName("메시지 복구 중 예외 발생 시 처리")
    void handleMessageRecovery_ShouldHandleException() {
        // Given
        when(chatMessageRepository.findUndeliveredMessages())
                .thenThrow(new RuntimeException("DB 오류가 발생했습니다."));

        // When
        chatFailureHandler.handleMessageRecovery();

        // Then
        verify(chatMessageRepository, never()).save(any());
        verify(webSocketChatService, never()).handleChatMessage(any());
    }
}
