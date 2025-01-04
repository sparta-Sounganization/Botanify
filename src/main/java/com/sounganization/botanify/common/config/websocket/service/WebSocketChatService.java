package com.sounganization.botanify.common.config.websocket.service;

import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.service.ChatMessageService;
import com.sounganization.botanify.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatService {
    private final MessageBroadcastService messageBroadcastService;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    public void handleChatMessage(ChatMessageReqDto chatMessage) {
        // WebSocket을 통해 처음 메시지가 올 때만 저장 및 Redis 발행
        if (chatMessage.type() == ChatMessageReqDto.MessageType.TALK) {
            // 저장된 메시지를 변수에 할당하여 사용
            ChatMessage savedMessage = chatMessageService.saveMessage(
                    chatMessage.roomId(),
                    chatMessage.senderId(),
                    chatMessage.content()
            );
            // 저장 성공 시에만 브로드캐스트 실행
            if (savedMessage != null) {
                messageBroadcastService.broadcastMessage(chatMessage.roomId(), chatMessage);
            }
        } else {
            // Redis 구독을 통해 받은 메시지는 브로드캐스트만 수행
            messageBroadcastService.broadcastMessage(chatMessage.roomId(), chatMessage);
        }
    }

    public void handleEnterRoom(WebSocketSession session, Long roomId, Long userId) {
        chatRoomService.getChatRoom(roomId, userId);
        messageBroadcastService.addSession(roomId, userId, session);
        log.info("사용자 {}가 채팅방 {}에 입장했습니다.", userId, roomId);
    }

    public void handleLeaveRoom(Long roomId, Long userId) {
        messageBroadcastService.removeSession(roomId, userId);
        log.info("사용자 {}가 채팅방 {}에서 퇴장했습니다.", userId, roomId);
    }
}
