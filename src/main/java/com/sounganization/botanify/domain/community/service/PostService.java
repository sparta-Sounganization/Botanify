package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.req.PostUpdateReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.CommentRepository;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;

    // 게시글 작성
    @Transactional
    public PostResDto createPost(PostReqDto postReqDto, Long userId) {

        //dto -> entity
        Post post = postMapper.reqDtoToEntity(postReqDto, userId);

        // DB 저장
        Post savedPost = postRepository.save(post);

        //entity -> dto
        return postMapper.entityToResDto(savedPost, HttpStatus.CREATED.value(), "게시글이 등록되었습니다");
    }

    // 게시글 조회 - 다건 조회
    public Page<PostListResDto> readPosts(int page, int size) {
        //pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        Page<Post> posts = postRepository.findAll(pageable);

        return posts.map(post -> postMapper.entityToResDto(post));
    }

    // 게시글 조회 - 단건조회
    @Transactional
    public PostWithCommentResDto readPost(Long postId) {
        // 게시글 조회
        Post post = getPostOrThrow(postId);

        // 조회수 증가
        post.incrementViewCounts();

        // 댓글과 대댓글 조회
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);

        // 댓글의 부모-자식 관계를 정리 (중복 제거)
        Map<Long, Comment> commentMap = new HashMap<>();
        List<Comment> uniqueComments = new ArrayList<>();

        for (Comment comment : comments) {
            if (comment.getParentComment() == null) {
                // 부모 댓글이면 새로운 리스트에 추가
                uniqueComments.add(comment);
            } else {
                // 자식 댓글의 부모 댓글에 추가
                Comment parent = comment.getParentComment();
                // 이미 부모 댓글에 자식 댓글이 있는지 확인
                if (!parent.getChildComments().contains(comment)) {
                    parent.getChildComments().add(comment);
                }
            }
            commentMap.put(comment.getId(), comment);
        }

        // 댓글 리스트를 DTO로 변환하여 반환
        return PostWithCommentResDto.from(post, uniqueComments);
    }

    // 게시글 수정
    @Transactional
    public PostResDto updatePost(Long postId, PostUpdateReqDto postUpdateReqDto, Long userId) {
        // 게시글 조회
        Post post = getPostOrThrow(postId);

        // 게시글 수정
        post.updatePost(postUpdateReqDto.getTitle(), postUpdateReqDto.getContent());

        // DB 저장
        Post savedPost = postRepository.save(post);

        //entity -> dto
        return postMapper.entityToResDto(savedPost, HttpStatus.CREATED.value(), "게시글이 수정되었습니다");
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPostOrThrow(postId);

        //이미 삭제된 게시글인지 확인
        if(post.isDeletedYn()){
            throw new CustomException(ExceptionStatus.POST_ALREADY_DELETED);
        }

        // 작성자 검증 나중에

        //삭제
        post.softDelete();

    }

    // 게시글 확인 메서드
    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.POST_NOT_FOUND));
    }
}
