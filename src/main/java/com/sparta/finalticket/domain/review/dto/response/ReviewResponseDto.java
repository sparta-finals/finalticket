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

    public ReviewResponseDto(String cachedReviewData) {
        JSONObject jsonObject = new JSONObject(cachedReviewData);
        this.id = jsonObject.getLong("id");
        this.review = jsonObject.getString("review");
        this.score = jsonObject.getLong("score");
        this.state = jsonObject.getBoolean("state");
        this.userId = jsonObject.getLong("userId");
        this.gameId = jsonObject.getLong("gameId");
    }
}

