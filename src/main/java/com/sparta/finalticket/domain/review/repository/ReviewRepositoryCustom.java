package com.sparta.finalticket.domain.review.repository;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryCustom {
    Optional<Review> findByGameId(Long id);

    List<ReviewResponseDto> findAllReviews();

    List<ReviewResponseDto> findReviewsByUserId(Long userId);

    List<ReviewResponseDto> findReviewsByScoreGreaterThan(int score);

    List<ReviewResponseDto> findReviewsByScoreLessThan(int score);
}
