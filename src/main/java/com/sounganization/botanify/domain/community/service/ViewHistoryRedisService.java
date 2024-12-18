package com.sounganization.botanify.domain.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class ViewHistoryRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    //조회이력 있는지 확인
    public boolean isViewHistoryExist(Long postId, Long userId, LocalDate viewedAt) {
        String redisKey = "view_history:post_id:" + postId;
        String field = String.valueOf(userId);
        String value = viewedAt.toString();

        String storedValue = (String) redisTemplate.opsForHash().get(redisKey, field);
        return value.equals(storedValue);
    }

    //캐시 저장
    public void saveViewHistory(Long postId, Long userId, LocalDate viewedAt) {
        String redisKey = "view_history:post_id:" + postId;
        String field = String.valueOf(userId);
        String value = viewedAt.toString();

        if (isOneDayPassed(viewedAt)) {
            redisTemplate.opsForHash().put(redisKey, field, value);
            long ttl = remainingTime();
            redisTemplate.expire(redisKey, ttl, TimeUnit.SECONDS);
        }
    }


    //하루계산
    public boolean isOneDayPassed(LocalDate viewedAt) {
        LocalDate today = LocalDate.now();
        return !viewedAt.isAfter(today);
    }

    //남은시간계산
    private long remainingTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), java.time.LocalTime.MIDNIGHT);
        return ChronoUnit.SECONDS.between(now, midnight);
    }
}
