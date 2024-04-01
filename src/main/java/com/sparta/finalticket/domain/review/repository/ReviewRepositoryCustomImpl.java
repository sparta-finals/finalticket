package com.sparta.finalticket.domain.review.repository;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    @Override
    public Optional<Review> findByGameId(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ReviewResponseDto> findAllReviews() {
        return null;
    }

    @Override
    public List<ReviewResponseDto> findReviewsByUserId(Long userId) {
        return null;
    }

    @Override
    public List<ReviewResponseDto> findReviewsByScoreGreaterThan(int score) {
        return null;
    }

    @Override
    public List<ReviewResponseDto> findReviewsByScoreLessThan(int score) {
        return null;
    }
}

