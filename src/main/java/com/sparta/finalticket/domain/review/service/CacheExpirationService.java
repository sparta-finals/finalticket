package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheExpirationService {

    private final RedisCacheService redisCacheService;
    private final ReviewService reviewService;

    // 매 1시간마다 캐시 갱신
    @Scheduled(fixedRate = 60 * 60 * 1000) // milliseconds
    public void refreshCacheHourly() {
        // 캐시 만료 시간 설정 (예: 1시간)
        Duration cacheExpiration = Duration.ofHours(1);

        // 모든 리뷰 데이터 조회
        List<Review> reviews = reviewService.getAllReviews();

        // 각 리뷰 데이터의 캐시를 업데이트하고 만료 시간 설정
        reviews.forEach(review -> redisCacheService.cacheReviewsData(review.getId(), review, cacheExpiration));
    }
}
