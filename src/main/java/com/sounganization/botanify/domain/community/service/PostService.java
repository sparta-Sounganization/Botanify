package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.req.PostUpdateReqDto;
import com.sounganization.botanify.domain.community.dto.res.CommentTempDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.CommentRepository;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import com.sounganization.botanify.domain.user.projection.UserProjection;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 게시글 작성
    @Transactional
    public CommonResDto createPost(PostReqDto postReqDto, Long userId) {
        //사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_NOT_FOUND));
        //dto -> entity
        Post post = postMapper.reqDtoToEntity(postReqDto, userId);
        // DB 저장
        Post savedPost = postRepository.save(post);
        //entity -> dto
        return postMapper.entityToResDto(savedPost, HttpStatus.CREATED);
    }

    // 게시글 조회 - 다건 조회
    public Page<PostListResDto> readPosts(int page, int size) {
        //pageable
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findAllByDeletedYnFalse(pageable);
        return posts.map(postMapper::entityToResDto);
    }

    // 게시글 조회 - 단건조회
    @Transactional
    public PostWithCommentResDto readPost(Long postId) {
        // 게시글 존재 여부 확인
        Post post = validatePost(postId);
        //이미 삭제된 게시글인지 확인
        checkPostNotDeleted(post);
        // 조회수 증가
        post.incrementViewCounts();
        // 댓글 조회
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);

        // userId로 username 매핑
        List<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<UserProjection> userProjections = userRepository.findUsernamesByIds(userIds);
        Map<Long, String> userMap = userProjections.stream()
                .collect(Collectors.toMap(UserProjection::getId, UserProjection::getUsername)
                );

        // 댓글을 Map 으로 그룹화 (ParentCommentId 기준)
        Map<Long, List<CommentTempDto>> commentMap = comments.stream()
                .map(comment -> new CommentTempDto(
                        comment.getId(),
                        comment.getUserId(),
                        userMap.getOrDefault(comment.getUserId(), "알수없는 유저"),
                        comment.getContent()
                ))
                .collect(Collectors.groupingBy(commentDto -> {
                    Comment parentComment = comments.stream()
                            .filter(c -> c.getId().equals(commentDto.commentId()))
                            .findFirst()
                            .orElse(null);

                    // parentComment 가 null 일 경우 루트 댓글로 처리
                    return (parentComment != null && parentComment.getParentComment() != null)
                            ? parentComment.getParentComment().getId()
                            : -1L; // -1L을 null 대신 사용 루트 댓글을 구분
                }));

        // 루트 댓글에 대댓글 매핑
        List<CommentTempDto> rootComments = commentMap.get(-1L);
        if (rootComments != null) {
            rootComments.forEach(comment -> {
                List<CommentTempDto> replies = commentMap.get(comment.commentId());
                if (replies != null) {
                    comment.replies().addAll(replies);
                }
            });
        }
        return postMapper.entityToResDto(post, rootComments != null ? rootComments : new ArrayList<>());
    }

    // 게시글 수정
    @Transactional
    public CommonResDto updatePost(Long postId, PostUpdateReqDto postUpdateReqDto, Long userId) {
        // 게시글 존재 여부 확인
        Post post = validatePost(postId);
        //소유자 확인
        validatePostOwner(post, userId);
        //이미 삭제된 게시글인지 확인
        checkPostNotDeleted(post);
        // 게시글 수정
        post.updatePost(postUpdateReqDto.title(), postUpdateReqDto.content());
        // DB 저장
        Post savedPost = postRepository.save(post);
        //entity -> dto
        return postMapper.entityToResDto(savedPost, HttpStatus.OK);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {
        //게시글 존재 여부 확인
        Post post = validatePost(postId);
        //게시글 소유자 확인
        validatePostOwner(post, userId);
        //이미 삭제된 게시글인지 확인
        checkPostNotDeleted(post);

        //게시글과 관련된 모든 댓글의 soft delete
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);
        comments.forEach(Comment::softDelete);
        //삭제
        post.softDelete();
    }

    // 게시글 존재 확인 메서드
    private Post validatePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.POST_NOT_FOUND));
    }


    // 게시글 소유자 확인
    private void validatePostOwner(Post post, Long userId) {
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ExceptionStatus.UNAUTHORIZED_POST_ACCESS);
        }
    }

    //이미 삭제된 게시글인지 확인
    private void checkPostNotDeleted(Post post) {
        if (post.isDeletedYn()) {
            throw new CustomException(ExceptionStatus.POST_ALREADY_DELETED);
        }
    }
}


