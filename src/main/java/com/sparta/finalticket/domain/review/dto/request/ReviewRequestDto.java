package com.sparta.finalticket.domain.review.dto;

import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private String review;
    private Long score;
    private Boolean state;
}
