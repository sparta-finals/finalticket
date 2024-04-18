package com.sparta.finalticket.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewCacheService {

    private final RedisService redisService;

    public void cacheReviewData(Long reviewId, String reviewData) {
        redisService.setValues("review_" + reviewId, reviewData);
    }

    public String getCachedReviewData(Long reviewId) {
        return redisService.getValues("review_" + reviewId);
    }

    public void clearReviewCache(Long reviewId) {
        redisService.deleteValues("review_" + reviewId);
    }
}
