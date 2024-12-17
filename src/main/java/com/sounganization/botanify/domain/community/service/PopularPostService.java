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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Scheduled(fixedRate = 3600000) // 1시간 마다
    @Transactional
    public void updateAllPostScores() {
        log.info("인기 게시글 점수 업데이트 시작");
        try {
            // 삭제되지 않은 모든 게시글 가져오기
            List<Post> activePosts = postRepository.findAllByDeletedYnFalse();

            // 게시글 IDs 가져오기
            List<Long> postIds = activePosts.stream()
                    .map(Post::getId)
                    .collect(Collectors.toList());

            // 한 번의 query로 모든 게시글에 대한 댓글 수를 가져오기
            Map<Long, Long> commentCounts = commentRepository.countCommentsByPostIds(postIds);

            // 점수 update
            for (Post post : activePosts) {
                int commentCount = commentCounts.getOrDefault(post.getId(), 0L).intValue();
                popularPostRedisRepository.updatePopularPost(post, commentCount);
            }

            log.info("총 {}개의 게시글 점수 업데이트 완료", activePosts.size());
        } catch (Exception e) {
            log.error("인기 게시글 점수 업데이트 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    // 게시글 삭제 시 인기 게시글에서도 제거
    @Transactional
    public void removeFromPopularPosts(Long postId) {
        popularPostRedisRepository.removePost(postId);
    }
}
