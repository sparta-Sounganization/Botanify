package com.sounganization.botanify.domain.chat.event;

import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageEvent {
    private final ChatMessageReqDto message;
}
