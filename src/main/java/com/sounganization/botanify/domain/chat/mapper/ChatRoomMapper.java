package com.sounganization.botanify.domain.chat.mapper;

import com.sounganization.botanify.domain.chat.dto.res.ChatRoomResDto;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "senderUserId", source = "senderUserId")
    @Mapping(target = "receiverUserId", source = "receiverUserId")
    @Mapping(target = "createdAt", source = "createdAt")
    ChatRoomResDto toResDto(ChatRoom chatRoom);

    default Page<ChatRoomResDto> toChatRoomResDtoPage(Page<ChatRoom> chatRooms) {
        return chatRooms.map(this::toResDto);
    }
}
