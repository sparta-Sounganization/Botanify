package com.sounganization.botanify.domain.chat.controller;

import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
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

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long receiverId
    ) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(userDetails.getId(), receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom);
    }

    @GetMapping("/rooms")
    public ResponseEntity<Page<ChatRoom>> getUserChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ChatRoom> chatRooms = chatRoomService.getUserChatRooms(
                userDetails.getId(),
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessage>> getRoomMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<ChatMessage> messages = chatMessageService.getRoomMessages(roomId, userDetails.getId(), pageable);
        return ResponseEntity.ok(messages);
    }
}
