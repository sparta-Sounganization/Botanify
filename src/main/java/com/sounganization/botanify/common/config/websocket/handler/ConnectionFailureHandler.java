package com.sounganization.botanify.common.config.websocket.handler;

import com.sounganization.botanify.common.config.websocket.service.WebSocketChatService;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.repository.ChatMessageRepository;
import com.sounganization.botanify.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionFailureHandler {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WebSocketChatService webSocketChatService;

    public void handleConnectionFailure(ChatMessageReqDto message) {
        log.error("인터넷 연결 실패 - 메시지 저장 후 재전송 대기");

        ChatMessage failedMessage = ChatMessage.builder()
                .type(convertToEntityMessageType(message.type()))
                .senderId(message.senderId())
                .content(message.content())
                .chatRoom(chatRoomRepository.findById(message.roomId())
                        .orElseThrow(() -> new CustomException(ExceptionStatus.CHAT_ROOM_NOT_FOUND)))
                .delivered(false)
                .build();

        chatMessageRepository.save(failedMessage);
    }

    public void attemptMessageRecovery() {
        log.info("인터넷 연결 복구 - 미전송 메시지 재전송 시작");
        try {
            List<ChatMessage> undeliveredMessages = chatMessageRepository.findUndeliveredMessages();

            for (ChatMessage message : undeliveredMessages) {
                retryMessageDelivery(message);
            }
        } catch (Exception e) {
            log.error("메시지 복구 실패: {}", e.getMessage());
        }
    }

    private void retryMessageDelivery(ChatMessage message) {
        ChatMessageReqDto messageDto = new ChatMessageReqDto(
                convertToDtoMessageType(message.getType()),
                message.getChatRoom().getId(),
                message.getSenderId(),
                message.getContent()
        );

        try {
            webSocketChatService.handleChatMessage(messageDto);
            message.markAsDelivered();
            chatMessageRepository.save(message);
            log.info("메시지 재전송 성공 - messageId: {}", message.getId());
        } catch (Exception e) {
            log.error("메시지 재전송 실패 - messageId: {}", message.getId());
        }
    }

    private ChatMessage.MessageType convertToEntityMessageType(ChatMessageReqDto.MessageType dtoType) {
        return ChatMessage.MessageType.valueOf(dtoType.name());
    }

    private ChatMessageReqDto.MessageType convertToDtoMessageType(ChatMessage.MessageType entityType) {
        return ChatMessageReqDto.MessageType.valueOf(entityType.name());
    }
}
