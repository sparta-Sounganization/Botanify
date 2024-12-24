package com.sounganization.botanify.domain.chat.mapper;

import com.sounganization.botanify.domain.chat.dto.res.ChatMessageResDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "createdAt", source = "createdAt")
    ChatMessageResDto toResDto(ChatMessage chatMessage);

    default Page<ChatMessageResDto> toChatMessageResDtoPage(Page<ChatMessage> messages) {
        return messages.map(this::toResDto);
    }
}
