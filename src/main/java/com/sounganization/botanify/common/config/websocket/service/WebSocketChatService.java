package com.sounganization.botanify.common.config.websocket.service;

import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
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

    public void handleChatMessage(ChatMessageReqDto chatMessage) {
        messageBroadcastService.broadcastMessage(chatMessage.roomId(), chatMessage);
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
