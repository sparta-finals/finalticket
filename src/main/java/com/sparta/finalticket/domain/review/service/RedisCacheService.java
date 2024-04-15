package com.sparta.finalticket.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheReviewStatistics(Long gameId) {
        // 해당 게임의 모든 리뷰 점수를 가져옴
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String reviewKey = "game:" + gameId.toString();
        long totalReviewCount = hashOps.size(reviewKey);

        // 모든 리뷰 점수의 합을 계산
        double totalScore = 0;
        for (String score : hashOps.values(reviewKey)) {
            totalScore += Double.parseDouble(score);
        }

        // 총 리뷰 수와 평균 평점을 Redis에 저장
        hashOps.put(reviewKey, "totalReviewCount", String.valueOf(totalReviewCount));
        hashOps.put(reviewKey, "averageScore", String.valueOf(totalScore / totalReviewCount));
    }
}
