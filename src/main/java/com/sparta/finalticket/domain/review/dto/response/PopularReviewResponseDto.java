package com.sparta.finalticket.domain.review.dto.response;

import com.sparta.finalticket.domain.comment.entity.Comment;
import com.sparta.finalticket.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PopularReviewResponseDto {
    private Long likeCount;
    private Long dislikeCount;
    private Long recommendationCount;
    private Long viewCount;

    public PopularReviewResponseDto(Review review) {
        this.likeCount = review.getLikeCount();
        this.dislikeCount = review.getDislikeCount();
        this.recommendationCount = review.getRecommendationCount();
        this.viewCount = review.getViewCount();
    }
}
