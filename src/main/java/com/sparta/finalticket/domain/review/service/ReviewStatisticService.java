package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewStatisticService {

    private final ReviewRepository reviewRepository;
    private final RedisReviewService redisReviewService;

    // 리뷰 통계 업데이트 메서드
    public void updateReviewStatistics(Long gameId) {
        // MySQL 데이터베이스에서 리뷰 총 수 조회
        Long totalReviewCount = getTotalReviewCount(gameId);

        // MySQL 데이터베이스에서 리뷰 평균 점수 조회
        Double averageReviewScore = getAverageReviewScore(gameId);

        // 조회된 값 출력
        printStatistics(totalReviewCount, averageReviewScore);

        // Redis에 업데이트
        updateRedisStatistics(gameId, totalReviewCount, averageReviewScore);
    }

    // MySQL 데이터베이스에서 리뷰 총 수 조회 메서드
    private Long getTotalReviewCount(Long gameId) {
        return reviewRepository.countByGameId(gameId);
    }

    // MySQL 데이터베이스에서 리뷰 평균 점수 조회 메서드
    private Double getAverageReviewScore(Long gameId) {
        return reviewRepository.calculateAverageScoreByGameId(gameId);
    }

    // 조회된 리뷰 통계 출력 메서드
    private void printStatistics(Long totalReviewCount, Double averageReviewScore) {
        System.out.println("MySQL 리뷰 총 수: " + totalReviewCount);
        System.out.println("MySQL 리뷰 평균 점수: " + averageReviewScore);
    }

    // Redis에 리뷰 통계 업데이트 메서드
    private void updateRedisStatistics(Long gameId, Long totalReviewCount, Double averageReviewScore) {
        redisReviewService.setTotalReviewCount(gameId, totalReviewCount);
        redisReviewService.setAverageReviewScore(gameId, averageReviewScore);
    }
}
