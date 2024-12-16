package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.dto.res.PopularPostResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PopularPostRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY = "popular_posts";

    // 점수와 함께 게시글을 Redis sorted set에 저장
    public void savePopularPost(PopularPostResDto post) {
        redisTemplate.opsForZSet().add(KEY + ":scores", String.valueOf(post.postId()), post.score());

        redisTemplate.opsForHash().put(
                KEY + ":details",
                String.valueOf(post.postId()),
                post
        );
    }

    // Top N 인기글 보기
    public List<PopularPostResDto> getTopNPosts(int limit) {
        Set<ZSetOperations.TypedTuple<String>> topPostsWithScores = redisTemplate.opsForZSet()
                .reverseRangeWithScores(KEY + ":scores", 0, limit - 1);

        if (topPostsWithScores == null || topPostsWithScores.isEmpty()) {
            return new ArrayList<>();
        }

        return topPostsWithScores.stream()
                .map(tuple -> (PopularPostResDto) redisTemplate.opsForHash()
                        .get(KEY + ":details", tuple.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 점수 계산 및 게시글 update
    public void updatePopularPost(Post post, int commentCount) {
        double score = (post.getViewCounts() * 0.4) + (commentCount * 0.6);

        PopularPostResDto popularPost = new PopularPostResDto(
                post.getId(),
                post.getTitle(),
                post.getViewCounts(),
                commentCount,
                score
        );

        savePopularPost(popularPost);
    }

    // 인기글에서 게시글 제거
    public void removePost(Long postId) {
        redisTemplate.opsForZSet().remove(KEY + ":scores", postId.toString());
        redisTemplate.opsForHash().delete(KEY + ":details", postId.toString());
    }
}
