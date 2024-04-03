package com.sparta.finalticket.domain.review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.QReview;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final QReview qReview = QReview.review1;

  @Override
  public Optional<Review> findByGameId(Long id) {
    Review review = jpaQueryFactory.selectFrom(qReview)
        .where(qReview.id.eq(id))
        .fetchOne();
    return Optional.ofNullable(review);
  }

  @Override
  public List<ReviewResponseDto> findAllReviews() {
    return jpaQueryFactory.select(Projections.constructor(ReviewResponseDto.class,
            qReview.id, qReview.score, qReview.id))
        .from(qReview)
        .fetch();
  }

  @Override
  public List<ReviewResponseDto> findReviewsByUserId(Long userId) {
    return jpaQueryFactory.select(Projections.constructor(ReviewResponseDto.class,
            qReview.id, qReview.score, qReview.id))
        .from(qReview)
        .where(qReview.id.eq(userId))
        .fetch();
  }

  @Override
  public List<ReviewResponseDto> findReviewsByScoreGreaterThan(int score) {
    return jpaQueryFactory.select(Projections.constructor(ReviewResponseDto.class,
            qReview.id, qReview.score, qReview.id))
        .from(qReview)
        .where(qReview.score.gt(score))
        .fetch();
  }

  @Override
  public List<ReviewResponseDto> findReviewsByScoreLessThan(int score) {
    return jpaQueryFactory.select(Projections.constructor(ReviewResponseDto.class,
            qReview.id, qReview.score, qReview.id))
        .from(qReview)
        .where(qReview.score.lt(score))
        .fetch();
  }

  @Override
  public List<ReviewResponseDto> getUserReviewList(User user) {
    return jpaQueryFactory.selectFrom(qReview).where(qReview.user.id.eq(user.getId())).fetch()
        .stream().map(ReviewResponseDto::new).toList();
  }
}

