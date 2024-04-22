package com.sparta.finalticket.domain.review.repository;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryCustom {

    Optional<Review> findReviewByIdAndStateTrue(Long reviewId);

    Optional<Review> findReviewByIdAndDeleteId(Long reviewId);

    Optional<Object> findReviewByGameIdAndReviewId(Long gameId, Long reviewId);

    List<Review> findByGameId(Long gameId);

    List<ReviewResponseDto> getUserReviewList(User user);
}
