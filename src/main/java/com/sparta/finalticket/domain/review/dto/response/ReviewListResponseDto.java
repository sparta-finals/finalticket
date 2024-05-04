package com.sparta.finalticket.domain.review.dto.response;

import com.sparta.finalticket.domain.review.entity.Genre;
import com.sparta.finalticket.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponseDto {

    private Long id;
    private String review;
    private Long score;
    private Boolean state;
    private Long userId;
    private Long gameId;
    private Long likeCount;
    private Long dislikeCount;
    private Long recommendationCount;
    private Long viewCount;
    private LocalDateTime reviewTime;
    private Double userTrustScore;
    private Genre genre;

    public ReviewListResponseDto(Review review) {
        this.id = review.getId();
        this.review = review.getReview();
        this.score = review.getScore();
        this.state = review.getState();
        this.userId = review.getUser() != null ? review.getUser().getId() : null;
        this.gameId = review.getGame() != null ? review.getGame().getId() : null;
        this.likeCount = review.getLikeCount();
        this.dislikeCount = review.getDislikeCount();
        this.recommendationCount = review.getRecommendationCount();
        this.viewCount = review.getViewCount();
        this.reviewTime = review.getReviewTime();
        this.userTrustScore = review.getUserTrustScore();
        this.genre = review.getGenre();
    }
}
