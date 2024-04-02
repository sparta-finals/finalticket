package com.sparta.finalticket.domain.review.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.finalticket.domain.review.entity.QReview;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ReviewResponseDto> getUserReviewList(User user) {
        QReview qReview = QReview.review1;
        return jpaQueryFactory.selectFrom(qReview).where(qReview.user.id.eq(user.getId())).fetchAll().stream().map(ReviewResponseDto::new).toList();
    }
}
