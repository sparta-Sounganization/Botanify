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

        Comment comment = CommentMapper.toEntity(requestDto, post, userId, null);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toResDto(savedComment);
    }

    @Transactional
    public CommentResDto createReply(Long parentCommentId, CommentReqDto requestDto, Long userId) {

        // todo - userId 존재 확인(mock 확인)
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.COMMENT_NOT_FOUND));

        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new CustomException(ExceptionStatus.INVALID_COMMENT_CONTENT);
        }

        Comment reply = CommentMapper.toEntity(requestDto, parentComment.getPost(), userId, parentComment);
        Comment savedReply = commentRepository.save(reply);

        return CommentMapper.toResDto(savedReply);
    }

    @Transactional
    public CommentResDto updateComment(Long commentId, CommentReqDto requestDto, Long userId) {

        // todo - userId 존재 확인(mock 확인)
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.COMMENT_NOT_FOUND));

        // 댓글 작성자 확인
        if(!comment.getUserId().equals(userId)){
            throw new CustomException(ExceptionStatus.NOT_COMMENT_OWNER);
        }

        comment.update(requestDto.getContent());

        return CommentMapper.toUpdateResDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {

        // todo - userId 존재 확인(mock 확인)
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.COMMENT_NOT_FOUND));

        // 댓글 작성자 확인
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException(ExceptionStatus.NOT_COMMENT_OWNER);
        }

        comment.softDelete();
    }
}
