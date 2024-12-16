package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.community.dto.res.PopularPostResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.CommentRepository;
import com.sounganization.botanify.domain.community.repository.PopularPostRedisRepository;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularPostService {

    private final PopularPostRedisRepository popularPostRedisRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;

    // 인기 게시글 목록 조회
    public List<PopularPostResDto> getPopularPosts(int limit) {
        return popularPostRedisRepository.getTopNPosts(limit);
    }

    // 게시글 점수 업데이트 (조회수나 댓글 수 변경 시 호출)
    @Transactional
    public void updatePostScore(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.POST_NOT_FOUND));

        // 삭제된 게시글 체크
        if (post.isDeletedYn()) {
            popularPostRedisRepository.removePost(postId);
            return;
        }

        // 댓글 수 계산
        int commentCount = commentRepository.findCommentsByPostId(postId).size();

        // Redis 업데이트
        popularPostRedisRepository.updatePopularPost(post, commentCount);
    }

    // 게시글 삭제 시 인기 게시글에서도 제거
    @Transactional
    public void removeFromPopularPosts(Long postId) {
        popularPostRedisRepository.removePost(postId);
    }
}
