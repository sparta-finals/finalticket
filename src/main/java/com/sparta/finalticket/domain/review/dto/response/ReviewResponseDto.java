package com.sparta.finalticket.domain.review.dto.response;

import com.sparta.finalticket.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

    private Long id;
    private String review;
    private Long score;
    private Boolean state;
    private Long userId;
    private Long gameId;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.review = review.getReview();
        this.score = review.getScore();
        this.state = review.getState();
        this.userId = review.getUser() != null ? review.getUser().getId() : null;
        this.gameId = review.getGame() != null ? review.getGame().getId() : null;
    }
}

