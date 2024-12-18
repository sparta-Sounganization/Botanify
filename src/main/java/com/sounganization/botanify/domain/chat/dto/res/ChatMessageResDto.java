package com.sounganization.botanify.domain.chat.dto.res;

import java.time.LocalDateTime;

public record ChatMessageResDto(
        Long id,
        MessageType type,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }
}
