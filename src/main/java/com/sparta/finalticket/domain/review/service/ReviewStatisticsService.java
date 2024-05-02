package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.dto.response.ReviewStatisticsResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewStatisticsService {

    private final ReviewRepository reviewRepository;

    public ReviewStatisticsResponseDto getReviewStatistics(Long gameId) {
        Long totalReviewCount = reviewRepository.countByGameId(gameId);
        Double averageReviewScore = reviewRepository.calculateAverageScoreByGameId(gameId);
        Long positiveReviewCount = reviewRepository.countPositiveReviews(gameId);
        Long negativeReviewCount = totalReviewCount - positiveReviewCount;

        return new ReviewStatisticsResponseDto(totalReviewCount, averageReviewScore, positiveReviewCount, negativeReviewCount);
    }
}
