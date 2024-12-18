package com.sounganization.botanify.domain.chat.controller;

import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.chat.dto.res.ChatMessageResDto;
import com.sounganization.botanify.domain.chat.dto.res.ChatRoomResDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import com.sounganization.botanify.domain.chat.mapper.ChatMessageMapper;
import com.sounganization.botanify.domain.chat.mapper.ChatRoomMapper;
import com.sounganization.botanify.domain.chat.service.ChatMessageService;
import com.sounganization.botanify.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ChatRoomMapper chatRoomMapper;
    private final ChatMessageMapper chatMessageMapper;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long receiverId
    ) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(userDetails.getId(), receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom);
    }

    @GetMapping("/rooms")
    public ResponseEntity<Page<ChatRoomResDto>> getUserChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ChatRoom> chatRooms = chatRoomService.getUserChatRooms(
                userDetails.getId(),
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(chatRoomMapper.toChatRoomResDtoPage(chatRooms));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResDto>> getRoomMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Page<ChatMessage> messages = chatMessageService.getRoomMessages(
                roomId,
                userDetails.getId(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );
        return ResponseEntity.ok(chatMessageMapper.toChatMessageResDtoPage(messages));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        chatMessageService.deleteMessage(messageId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        chatRoomService.deleteChatRoom(roomId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
