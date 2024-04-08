package com.sparta.finalticket.domain.review.repository;

import static com.sparta.finalticket.domain.review.entity.QReview.review1;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.game.entity.QGame;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Review> findByGameId(Long id) {
        Review review = jpaQueryFactory.selectFrom(review1)
            .where(review1.id.eq(id))
            .fetchOne();
        return Optional.ofNullable(review);
    }

    @Override
    public List<ReviewResponseDto> findAllReviews() {
        return jpaQueryFactory.select(Projections.constructor(ReviewResponseDto.class,
                review1.id, review1.score, review1.id))
            .from(review1)
            .fetch();
    }

    @Override
    public List<ReviewResponseDto> findReviewsByUserId(Long userId) {
        return jpaQueryFactory.select(Projections.constructor(ReviewResponseDto.class,
                review1.id, review1.score, review1.id))
            .from(review1)
            .where(review1.id.eq(userId))
            .fetch();
    }

    @Override
    public List<ReviewResponseDto> findReviewsByScoreGreaterThan(int score) {
        return jpaQueryFactory.select(Projections.constructor(ReviewResponseDto.class,
                review1.id, review1.score, review1.id))
            .from(review1)
            .where(review1.score.gt(score))
            .fetch();
    }

    @Override
    public List<ReviewResponseDto> findReviewsByScoreLessThan(int score) {
        return jpaQueryFactory.select(Projections.constructor(ReviewResponseDto.class,
                review1.id, review1.score, review1.id))
            .from(review1)
            .where(review1.score.lt(score))
            .fetch();
    }

    @Override
    public List<ReviewResponseDto> getUserReviewList(User user) {
        try{
            return jpaQueryFactory.selectFrom(review1)
                .leftJoin(review1.game, QGame.game)
                .fetchJoin()
                .where(review1.user.id.eq(user.getId()))
                .fetch().stream().map(ReviewResponseDto::new).toList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

