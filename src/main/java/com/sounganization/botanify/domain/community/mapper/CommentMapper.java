package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.HttpStatus;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", source = "post")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "parentComment", source = "parentComment")
    @Mapping(target = "content", source = "requestDto.content")
    @Mapping(target = "childComments", ignore = true)
    @Mapping(target = "depth", expression = "java(parentComment == null ? 0 : parentComment.getDepth() + 1)")
    Comment toEntity(CommentReqDto requestDto, Post post, Long userId, Comment parentComment);

    default CommonResDto toResDto(Comment comment) {
        return new CommonResDto(HttpStatus.CREATED,"댓글이 추가되었습니다.", comment.getId());
    }

    default CommonResDto toUpdateResDto(Comment comment) {
        return new CommonResDto(HttpStatus.OK, "댓글이 수정되었습니다." , comment.getId());
    }
}
