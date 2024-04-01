package com.sparta.finalticket.domain.review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.QReview;
import com.sparta.finalticket.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {


    private final JPAQueryFactory queryFactory;
    private final QReview qReview = QReview.review;

    @Override
    public Optional<Review> findByGameId(Long id) {
        Review review = queryFactory
            .selectFrom(qReview)
            .where(qReview.game.id.eq(id))
            .fetchOne();
        return Optional.ofNullable(review);
    }

    @Override
    public List<ReviewResponseDto> findAllReviews() {
        return queryFactory
            .select(Projections.constructor(ReviewResponseDto.class, qReview))
            .from(qReview)
            .fetch();
    }

    @Override
    public List<ReviewResponseDto> findReviewsByUserId(Long userId) {
        return queryFactory
            .select(Projections.constructor(ReviewResponseDto.class, qReview))
            .from(qReview)
            .where(qReview.user.id.eq(userId))
            .fetch();
    }

    @Override
    public List<ReviewResponseDto> findReviewsByScoreGreaterThan(int score) {
        return queryFactory
            .select(Projections.constructor(ReviewResponseDto.class, qReview))
            .from(qReview)
            .where(qReview.score.gt(score))
            .fetch();
    }

    @Override
    public List<ReviewResponseDto> findReviewsByScoreLessThan(int score) {
        return queryFactory
            .select(Projections.constructor(ReviewResponseDto.class, qReview))
            .from(qReview)
            .where(qReview.score.lt(score))
            .fetch();
    }
}

