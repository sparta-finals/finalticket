package com.sparta.finalticket.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {


    private final RedisTemplate<String, String> redisTemplate;

    public void saveReviewScore(Long gameId, Long reviewId, Long score) {
        Double doubleScore = score.doubleValue(); // Long 타입을 Double로 변환
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String reviewKey = "game:" + gameId.toString();
        hashOps.put(reviewKey, reviewId.toString(), doubleScore.toString()); // Convert to String before putting into Redis
    }

    public void getReviewScore(Long gameId, Long reviewId) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String reviewKey = "game:" + gameId.toString();
        hashOps.delete(reviewKey, reviewId.toString());
    }

    public void updateReviewScore(Long gameId, Long reviewId, Long score) {
        Double doubleScore = score.doubleValue(); // Long 타입을 Double로 변환
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String reviewKey = "game:" + gameId.toString();
        hashOps.put(reviewKey, reviewId.toString(), doubleScore.toString()); // Convert to String before putting into Redis
    }

    public void deleteReviewScore(Long gameId, Long reviewId) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String reviewKey = "game:" + gameId.toString();
        hashOps.delete(reviewKey, reviewId.toString());
    }
}
