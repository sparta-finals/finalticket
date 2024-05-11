package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisService redisService;

    public void createReview(Long reviewId, Review createdReviewData) {
        cacheReviewData(reviewId, createdReviewData);
    }

    public void getReview(Long reviewId, Review getReviewData) {
        cacheReviewData(reviewId, getReviewData);
    }

    public void updateReview(Long reviewId, Review updatedReviewData) {
        cacheReviewData(reviewId, updatedReviewData);
    }

    public void updateReviews(Long reviewId, Review review) {
        redisService.cacheReviewsData("review_" + reviewId, review.toString(), Duration.ofMinutes(30)); // 예시로 30분 유효 기간 설정
    }

    public void cacheReviewData(Long reviewId, Review review) {
        redisService.cacheReviewsData("review_" + reviewId, review.toString(), Duration.ofMinutes(30)); // 예시로 30분 유효 기간 설정
    }

    public Object getCachedData(String cacheKey) {
        return redisService.getValues(cacheKey);
    }

    public void cacheData(String cacheKey, Object data) {
        redisService.cacheReviewsData(cacheKey, data.toString(), Duration.ofMinutes(30)); // 예시로 30분 유효 기간 설정
    }

    public String getCachedReviewData(Long reviewId) {
        return redisService.getValues("review_" + reviewId);
    }

    public void clearReviewCache(Long reviewId) {
        redisService.deleteValues("review_" + reviewId);
    }

    // 캐시 만료 시간 설정
    public void cacheReviewsData(Long reviewId, Review review, Duration duration) {
        redisService.setValues("review_" + reviewId, review.toString(), duration);
    }

    public void cacheReviewsDataWithDuration(Long reviewId, Review review, Duration duration) {
        // 리뷰 데이터를 Redis에 캐시하고 유효 기간을 설정합니다.
        redisService.cacheReviewsData("review_" + reviewId, review.toString(), duration);
    }

    // 만료 시간 설정
    public void expire(Long reviewId, long seconds) {
        redisService.expire("review_" + reviewId, seconds);
    }


    // 매개변수 없는 버전의 clearReviewCache 메서드 추가
    public void clearReviewCache() {
        Set<String> keys = redisService.getAllKeys("review_*");
        keys.forEach(redisService::deleteValues);
    }

    public void clearGameCache(Long gameId) {
        Set<String> keys = redisService.getAllKeys("review_" + gameId + "_*");
        keys.forEach(redisService::deleteValues);
    }

    public void clearAllReviews() {
        Set<String> keys = redisService.getAllKeys("review_*");
        keys.forEach(redisService::deleteValues);
    }
}
