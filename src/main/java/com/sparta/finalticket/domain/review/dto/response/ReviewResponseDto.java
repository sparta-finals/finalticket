package com.sparta.finalticket.domain.review.dto.response;

import com.sparta.finalticket.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String review;
    private Long score;
    private Long userId;
    private Long gameId;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.review = review.getReview();
        this.score = review.getScore();
        this.userId = review.getUser().getId();
        this.gameId = review.getGame().getId();
    }
}
