package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.domain.community.dto.req.ViewHistoryDto;
import com.sounganization.botanify.domain.community.entity.ViewHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ViewHistoryMapper {
    @Mapping(target = "id", ignore = true)
    ViewHistory dtoToEntity(ViewHistoryDto viewHistoryDto);
}
