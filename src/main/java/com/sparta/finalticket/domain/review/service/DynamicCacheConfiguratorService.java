package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DynamicCacheConfiguratorService {

    private final RedisCacheService redisCacheService;

    // 시간대별로 데이터 접근을 모니터링하고, 필요에 따라 캐시의 유효 기간을 동적으로 조정
    public void monitorCacheHits(Long reviewId, Review review) {
        LocalTime currentTime = LocalTime.now();

        // 특정 시간대에 데이터 접근이 증가하는 시간 범위를 설정
        LocalTime peakStartTime = LocalTime.of(8, 0); // 예시: 아침 8시
        LocalTime peakEndTime = LocalTime.of(10, 0); // 예시: 아침 10시

        if (currentTime.isAfter(peakStartTime) && currentTime.isBefore(peakEndTime)) {
            // 아침 시간대에는 캐시의 유효 기간을 더 짧게 설정하여 더 자주 업데이트되도록 함
            adjustCacheDuration(reviewId, review, Duration.ofMinutes(30)); // 예시: 30분
        } else {
            // 다른 시간대에는 기본적인 캐시 유효 기간을 사용
            adjustCacheDuration(reviewId, review, Duration.ofHours(1)); // 예시: 1시간
        }
    }

    // 캐시의 유효 기간을 조정하는 메서드
    public void adjustCacheDuration(Long reviewId, Review review, Duration duration) {
        // 캐시의 유효 기간을 조정하기 위해 RedisCacheService의 캐시 메서드를 사용
        redisCacheService.cacheReviewsDataWithDuration(reviewId, review, duration);
    }
}
