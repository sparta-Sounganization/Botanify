package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.community.dto.res.CommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;

import java.util.ArrayList;

public class CommentMapper {

    public static Comment toEntity(CommentReqDto requestDto, Post post, Long userId) {
        return Comment.builder()
                .content(requestDto.getContent())
                .post(post)
                .userId(userId)
                .parentComment(null)
                .childComments(new ArrayList<>())
                .build();
    }

    public static CommentResDto toResDto(Comment comment) {
        return CommentResDto.builder()
                .message("댓글이 추가되었습니다.")
                .id(comment.getId())
                .build();
    }
}
