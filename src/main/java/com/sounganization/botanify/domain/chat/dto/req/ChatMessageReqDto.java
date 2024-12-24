package com.sounganization.botanify.domain.chat.dto.req;

public record ChatMessageReqDto(
        MessageType type,
        Long roomId,
        Long senderId,
        String content
) {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }
}
