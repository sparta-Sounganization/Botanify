package com.sounganization.botanify.domain.chat.dto.res;

import java.time.LocalDateTime;

public record ChatRoomResDto(
        Long id,
        Long senderUserId,
        Long receiverUserId,
        LocalDateTime createdAt
) {}
