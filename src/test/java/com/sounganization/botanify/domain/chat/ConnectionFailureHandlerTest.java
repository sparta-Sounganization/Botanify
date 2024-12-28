package com.sounganization.botanify.domain.chat;

import com.sounganization.botanify.common.config.websocket.handler.ConnectionFailureHandler;
import com.sounganization.botanify.common.config.websocket.service.WebSocketChatService;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import com.sounganization.botanify.domain.chat.repository.ChatMessageRepository;
import com.sounganization.botanify.domain.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionFailureHandlerTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private WebSocketChatService webSocketChatService;

    @InjectMocks
    private ConnectionFailureHandler connectionFailureHandler;

    @Test
    @DisplayName("인터넷 연결 실패 시 메시지 저장")
    void handleConnectionFailure_ShouldSaveMessage() {
        // Given
        ChatRoom chatRoom = ChatRoom.builder()
                .id(1L)
                .build();

        ChatMessageReqDto message = new ChatMessageReqDto(
                ChatMessageReqDto.MessageType.TALK,
                1L,
                1L,
                "Test message"
        );

        when(chatRoomRepository.findById(1L))
                .thenReturn(Optional.of(chatRoom));

        // When
        connectionFailureHandler.handleConnectionFailure(message);

        // Then
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("인터넷 연결 복구 후 미전송 메시지 재전송")
    void attemptMessageRecovery_ShouldRetryUndeliveredMessages() {
        // Given
        ChatRoom chatRoom = ChatRoom.builder()
                .id(1L)
                .build();

        ChatMessage undeliveredMessage = ChatMessage.builder()
                .id(1L)
                .type(ChatMessage.MessageType.TALK)
                .senderId(1L)
                .content("미전송 메시지")
                .chatRoom(chatRoom)
                .delivered(false)
                .build();

        when(chatMessageRepository.findUndeliveredMessages())
                .thenReturn(List.of(undeliveredMessage));

        // When
        connectionFailureHandler.attemptMessageRecovery();

        // Then
        verify(webSocketChatService).handleChatMessage(any(ChatMessageReqDto.class));
        verify(chatMessageRepository).save(undeliveredMessage);
        assertTrue(undeliveredMessage.getDelivered());
    }

    @Test
    @DisplayName("미전송 메시지 재전송 실패 시 예외 처리")
    void attemptMessageRecovery_ShouldHandleException() {
        // Given
        when(chatMessageRepository.findUndeliveredMessages())
                .thenThrow(new RuntimeException("DB 오류가 발생했습니다."));

        // When
        connectionFailureHandler.attemptMessageRecovery();

        // Then
        verify(chatMessageRepository, never()).save(any());
        verify(webSocketChatService, never()).handleChatMessage(any());
    }
}
