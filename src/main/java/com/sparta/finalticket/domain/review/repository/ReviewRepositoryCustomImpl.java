package com.sparta.finalticket.domain.review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.game.entity.QGame;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.QReview;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Review> findReviewByIdAndStateTrue(Long reviewId) {
        return Optional.ofNullable(
            jpaQueryFactory.selectFrom(QReview.review1)
                .where(QReview.review1.id.eq(reviewId)
                    .and(QReview.review1.state.isTrue()))
                .fetchOne()
        );
    }

    @Override
    public Optional<Review> findReviewByIdAndDeleteId(Long reviewId) {
        System.out.println("실행됨!!!!@#!@#!@#!@#!@#!@#!@#!@#!@#");
        return Optional.ofNullable(
            jpaQueryFactory.selectFrom(QReview.review1)
                .where(QReview.review1.id.eq(reviewId))
                .fetchOne()
        );
    }

    @Override
    public Optional<Review> findReviewByGameIdAndReviewId(Long gameId, Long reviewId) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(QReview.review1)
                        .where(QReview.review1.game.id.eq(gameId)
                                .and(QReview.review1.id.eq(reviewId)))
                        .fetchOne()
        );
    }

    @Override
    public List<Review> findByGameId(Long gameId) {
        return jpaQueryFactory.selectFrom(QReview.review1)
            .where(QReview.review1.game.id.eq(gameId))
            .fetch();
    }

    @Override
    public List<Review> findTopPopularReviewsByGameId(Long gameId, Pageable pageable) {
        QReview review = QReview.review1;
        return jpaQueryFactory.selectFrom(review)
                .where(review.game.id.eq(gameId))
                .orderBy(review.viewCount.desc())
                .fetch();
    }

    @Override
    public List<ReviewResponseDto> getUserReviewList(User user) {
        try{
            return jpaQueryFactory.selectFrom(QReview.review1)
                .leftJoin(QReview.review1.game, QGame.game)
                .fetchJoin()
                .where(QReview.review1.user.id.eq(user.getId()))
                .fetch().stream().map(ReviewResponseDto::new).toList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
