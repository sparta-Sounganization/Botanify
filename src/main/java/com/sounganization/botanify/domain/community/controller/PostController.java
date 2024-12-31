package com.sounganization.botanify.domain.community.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.common.util.JwtUtil;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.req.PostUpdateReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    //게시글 작성
    @PostMapping
    public ResponseEntity<CommonResDto> createPost(@Valid @RequestBody PostReqDto postReqDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommonResDto postResDto = postService.createPost(postReqDto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postResDto);
    }

    //게시글 조회 - 다건조회
    @GetMapping
    public ResponseEntity<Page<PostListResDto>> readPosts(
            @CookieValue(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean local,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String town,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dateBefore
    ) {
        UserDetailsImpl userDetails = null;
        if (Objects.nonNull(token)) {
            userDetails = ((UserDetailsImpl)jwtUtil.getAuthentication(token).getPrincipal());
        }

        Page<PostListResDto> postListResDto = postService.readPosts(
                userDetails, page, size, local, sortBy, order, city, town, search, dateBefore);
        return ResponseEntity.ok(postListResDto);
    }

    //게시글 조회 - 단건조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostWithCommentResDto> readPost(@PathVariable Long postId,
                                                          @CookieValue(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null) {
            Authentication authentication = jwtUtil.getAuthentication(token);
            userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        }
        PostWithCommentResDto postWithCommentResDto = postService.readPost(postId, userId);
        return ResponseEntity.ok(postWithCommentResDto);
    }

    //게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<CommonResDto> updatePost(@PathVariable Long postId,
                                                   @Valid @RequestBody PostUpdateReqDto postUpdateReqDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommonResDto postResDto = postService.updatePost(postId, postUpdateReqDto, userDetails.getId());
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
