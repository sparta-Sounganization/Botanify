package com.sounganization.botanify.domain.community.controller;

import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.req.PostUpdateReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시글 작성
    @PostMapping
    public ResponseEntity<PostResDto> createPost(@Valid @RequestBody PostReqDto postReqDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostResDto postResDto = postService.createPost(postReqDto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postResDto);
    }

    //게시글 조회 - 다건조회
    @GetMapping
    public ResponseEntity<Page<PostListResDto>> readPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostListResDto> postListResDto = postService.readPosts(page, size);
        return ResponseEntity.ok(postListResDto);
    }

    //게시글 조회 - 단건조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostWithCommentResDto> readPost(@PathVariable Long postId) {
        PostWithCommentResDto postWithCommentResDto = postService.readPost(postId);
        return ResponseEntity.ok(postWithCommentResDto);
    }

    //게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResDto> updatePost(@PathVariable Long postId,
                                                 @Valid @RequestBody PostUpdateReqDto postUpdateReqDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostResDto postResDto = postService.updatePost(postId, postUpdateReqDto, userDetails.getId());
        return ResponseEntity.ok(postResDto);
    }

    //게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(postId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
