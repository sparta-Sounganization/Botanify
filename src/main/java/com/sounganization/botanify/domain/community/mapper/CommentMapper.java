package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.community.dto.res.CommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", source = "post")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "parentComment", source = "parentComment")
    @Mapping(target = "content", source = "requestDto.content")
    @Mapping(target = "childComments", ignore = true)
    Comment toEntity(CommentReqDto requestDto, Post post, Long userId, Comment parentComment);

    @Mapping(target = "message", constant = "댓글이 추가되었습니다.")
    CommentResDto toResDto(Comment comment);

    @Mapping(target = "message", constant = "댓글이 수정되었습니다.")
    CommentResDto toUpdateResDto(Comment comment);
}
