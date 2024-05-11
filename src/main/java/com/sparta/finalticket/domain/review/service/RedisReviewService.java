package com.sparta.finalticket.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisReviewService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedissonClient redissonClient;

    // 게임의 리뷰 총수를 Redis에 저장하는 메서드
    public void setTotalReviewCount(Long gameId, Long totalReviewCount) {
        redisTemplate.opsForValue().set("game:" + gameId + ":totalReviewCount", totalReviewCount);
    }

    public Long getTotalReviewCount(Long gameId) {
        Object value = redisTemplate.opsForValue().get("game:" + gameId + ":totalReviewCount");
        return value != null ? ((Number) value).longValue() : 0L;
    }

    // 게임의 리뷰 평균 점수를 Redis에 저장하는 메서드
    public void setAverageReviewScore(Long gameId, Double averageReviewScore) {
        redisTemplate.opsForValue().set("game:" + gameId + ":averageReviewScore", averageReviewScore);
    }

    // 게임의 리뷰 평균 점수를 Redis에서 가져오는 메서드
    public Double getAverageReviewScore(Long gameId) {
        Object value = redisTemplate.opsForValue().get("game:" + gameId + ":averageReviewScore");
        return value != null ? ((Number) value).doubleValue() : 0.0;
    }

    public boolean checkOptimisticLock(Long gameId, long expectedVersion) {
        long actualVersion = redissonClient.getAtomicLong("reviewVersion:" + gameId).get();
        return actualVersion == expectedVersion;
    }
}
