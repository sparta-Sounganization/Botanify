package com.sounganization.botanify.domain.community.controller;

import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.community.dto.res.CommentResDto;
import com.sounganization.botanify.domain.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentReqDto requestDto,
            @RequestParam Long userId // 임시로 더미 userId를 받음
    ) {

        CommentResDto responseDto = commentService.createComment(postId, requestDto, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PostMapping("/{parentCommentId}/replies")
    public ResponseEntity<CommentResDto> createReply(
            @PathVariable Long parentCommentId,
            @Valid @RequestBody CommentReqDto requestDto,
            @RequestParam Long userId // 임시로 더미 userId를 받음
    ) {

        CommentResDto responseDto = commentService.createReply(parentCommentId, requestDto, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResDto> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentReqDto requestDto,
            @RequestParam Long userId // 임시로 더미 userId를 받음
    ) {

        CommentResDto responseDto = commentService.updateComment(id, requestDto, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @RequestParam Long userId // 임시로 더미 userId를 받음
    ) {
        commentService.deleteComment(id, userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
