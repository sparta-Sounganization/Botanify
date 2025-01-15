package com.sounganization.botanify.common.config.websocket.service;

import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatService {
    private final MessageBroadcastService messageBroadcastService;
    private final ChatMessageService chatMessageService;

    public void handleChatMessage(ChatMessageReqDto chatMessage) {
        log.info("handleChatMessage 호출됨, 메시지: {}", chatMessage);

        if (chatMessage.type() != ChatMessageReqDto.MessageType.TALK ||
                chatMessage.source() != ChatMessageReqDto.MessageSource.WEBSOCKET) {
            log.warn("메시지가 거부되었습니다: 잘못된 type 또는 source입니다. Type: {}, Source: {}",
                    chatMessage.type(), chatMessage.source());
            return;
        }

        try {
            log.info("Room: {}, sender: {}에 대한 메시지 생성 중",
                    chatMessage.roomId(), chatMessage.senderId());

            ChatMessage message = chatMessageService.createMessage(
                    chatMessage.roomId(),
                    chatMessage.senderId(),
                    chatMessage.content()
            );

            if (message != null) {
                log.info("메시지가 생성되었습니다. 저장 시도 중");
                Future<ChatMessage> future = chatMessageService.saveMessageAsync(message);
                ChatMessage savedMessage = future.get(5, TimeUnit.SECONDS);

                if (savedMessage != null) {
                    log.info("메시지가 저장되었습니다. 방 {}에 브로드캐스트 중", chatMessage.roomId());
                    messageBroadcastService.broadcastMessage(chatMessage.roomId(), chatMessage);
                }
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("채팅 메시지 처리 실패: ", e);
        }
    }

    public void handleEnterRoom(WebSocketSession session, Long roomId, Long userId) {
        messageBroadcastService.addSession(roomId, userId, session);
        log.info("사용자 {}가 채팅방 {}에 입장했습니다.", userId, roomId);
    }

    public void handleLeaveRoom(Long roomId, Long userId) {
        messageBroadcastService.removeSession(roomId, userId);
        log.info("사용자 {}가 채팅방 {}에서 퇴장했습니다.", userId, roomId);
    }
}
