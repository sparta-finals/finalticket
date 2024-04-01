package com.sparta.finalticket.domain.review.dto;

import com.sparta.finalticket.domain.review.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private String review;
    private Long score;
    private Boolean state;

    public ReviewRequestDto(Review review) {
        this.review = review.getReview();
        this.score = review.getScore();
        this.state = review.getState();
    }
}
