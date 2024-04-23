package com.sparta.finalticket.domain.review.dto.response;

import com.sparta.finalticket.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAspectResponseDto {

    private Long id;
    private String review;
    private Long score;
    private Boolean state;
    private Long userId;
    private Long gameId;

    public ReviewAspectResponseDto(Review review) {
        this.id = review.getId();
        this.review = review.getReview();
        this.score = review.getScore();
        this.state = review.getState();
        this.userId = review.getUser() != null ? review.getUser().getId() : null;
        this.gameId = review.getGame() != null ? review.getGame().getId() : null;
    }

    public ReviewAspectResponseDto(String reviewData) {
        if (reviewData == null || reviewData.isEmpty()) {
            throw new IllegalArgumentException("리뷰 데이터는 null 이거나 비어 있을 수 없습니다.");
        }

        String[] fields = reviewData.split(",");
        this.id = Long.parseLong(fields[0]);
        this.review = fields[1];
        this.score = Long.parseLong(fields[2]);
        this.state = Boolean.parseBoolean(fields[3]);
        this.userId = Long.parseLong(fields[4]);
        this.gameId = Long.parseLong(fields[5]);
    }
}

