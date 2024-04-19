package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewStatisticService {

    private final ReviewRepository reviewRepository;
    private final RedisReviewService redisReviewService;

    public void updateReviewStatistics(Long gameId) {
        // 게임별 리뷰 총 수 업데이트
        Long totalReviewCount = reviewRepository.countByGameId(gameId);
        redisReviewService.setTotalReviewCount(gameId, totalReviewCount);

        // 게임별 리뷰 평균 점수 업데이트
        Double averageReviewScore = reviewRepository.calculateAverageScoreByGameId(gameId);
        redisReviewService.setAverageReviewScore(gameId, averageReviewScore);
    }
}
