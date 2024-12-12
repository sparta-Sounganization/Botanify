package com.sounganization.botanify.domain.user.mapper;

import com.sounganization.botanify.domain.user.dto.res.UserResDto;
import com.sounganization.botanify.domain.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResDto toResDto(User user);
}
