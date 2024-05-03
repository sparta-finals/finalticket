package com.sparta.finalticket.domain.review.repository;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Genre;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryCustom {

    Optional<Review> findReviewByIdAndStateTrue(Long reviewId);

    Optional<Review> findReviewByIdAndDeleteId(Long reviewId);

    Optional<Review> findReviewByGameIdAndReviewId(Long gameId, Long reviewId);

    List<Review> findByGameId(Long gameId);

    List<Review> findTopPopularReviewsByGameId(Long gameId, Pageable pageable);

    List<ReviewResponseDto> getUserReviewList(User user);
}
