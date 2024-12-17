package com.sounganization.botanify.domain.chat.dto;

public record ChatMessageDto(
        MessageType type,
        Long roomId,
        Long senderId,
        String content
) {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }
}
