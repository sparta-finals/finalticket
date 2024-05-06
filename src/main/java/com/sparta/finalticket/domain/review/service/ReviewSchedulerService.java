package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewSchedulerService {

    private final ReviewService reviewService;
    private final RedisCacheService redisCacheService;

    // 매일 밤 12시에 실행되는 스케줄러
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshCacheAtMidnight() {
        // Redis에 저장된 모든 리뷰 데이터의 캐시를 갱신하고 모든 리뷰 통계를 업데이트
        updateCacheAndStatistics();
    }

    // 특정 시간마다 실행되는 스케줄러
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // milliseconds
    public void refreshCachePeriodically() {
        // 모든 리뷰 데이터의 캐시를 갱신하고 모든 리뷰 통계를 업데이트
        updateCacheAndStatistics();
    }

    // 매일 오전 1시에 실행되는 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    public void updateReviewStatisticsAtOneAM() {
        // 모든 게임의 리뷰 통계 업데이트
        reviewService.updateReviewStatisticsForAllGames();
    }

    // 캐시 갱신과 리뷰 통계 업데이트를 한 번에 수행하는 메서드
    private void updateCacheAndStatistics() {
        // 모든 리뷰 데이터 조회
        List<Review> reviews = reviewService.getAllReviews();

        // 각 리뷰 데이터의 캐시를 업데이트
        reviews.forEach(review -> redisCacheService.updateReviews(review.getId(), review));

        // 모든 리뷰 통계 업데이트
        reviewService.updateReviewStatisticsForAllGames();
    }
}
