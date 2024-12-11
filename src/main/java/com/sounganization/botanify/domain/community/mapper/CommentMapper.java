package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.community.dto.res.CommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;

import java.util.ArrayList;

public class CommentMapper {

    public static Comment toEntity(CommentReqDto requestDto, Post post, Long userId, Comment parentComment) {
        return Comment.builder()
                .content(requestDto.content())
                .post(post)
                .userId(userId)
                .parentComment(parentComment)
                .childComments(new ArrayList<>())
                .build();
    }

    public static CommentResDto toResDto(Comment comment) {
        return new CommentResDto(
                "댓글이 추가되었습니다.",
                comment.getId()
        );
    }

    public static CommentResDto toUpdateResDto(Comment comment) {
        return new CommentResDto(
                "댓글이 수정되었습니다.",
                comment.getId()
        );
    }
}
