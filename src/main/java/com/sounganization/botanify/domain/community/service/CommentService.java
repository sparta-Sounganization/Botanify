package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.community.dto.res.CommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.CommentMapper;
import com.sounganization.botanify.domain.community.repository.CommentRepository;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResDto createComment(Long postId, CommentReqDto requestDto, Long userId) {

        // todo - userId 존재 확인(mock 확인)
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.POST_NOT_FOUND));

        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new CustomException(ExceptionStatus.INVALID_COMMENT_CONTENT);
        }

        Comment comment = CommentMapper.toEntity(requestDto, post, userId);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toResDto(savedComment);
    }
}
