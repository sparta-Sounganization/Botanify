package com.sounganization.botanify.domain.community.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommonResDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentReqDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        CommonResDto responseDto = commentService.createComment(postId, requestDto, userDetails.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PostMapping("/{parentCommentId}/replies")
    public ResponseEntity<CommonResDto> createReply(
            @PathVariable Long parentCommentId,
            @Valid @RequestBody CommentReqDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        CommonResDto responseDto = commentService.createReply(parentCommentId, requestDto, userDetails.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResDto> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentReqDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        CommonResDto responseDto = commentService.updateComment(id, requestDto, userDetails.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        commentService.deleteComment(id, userDetails.getId());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
