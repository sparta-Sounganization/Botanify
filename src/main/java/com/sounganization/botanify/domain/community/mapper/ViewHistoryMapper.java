package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.domain.community.dto.req.ViewHistoryDto;
import com.sounganization.botanify.domain.community.entity.ViewHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ViewHistoryMapper {
    ViewHistory dtoToEntity(ViewHistoryDto viewHistoryDto);
}
