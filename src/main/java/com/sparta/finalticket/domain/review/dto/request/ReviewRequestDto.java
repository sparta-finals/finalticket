package com.sparta.finalticket.domain.review.dto.request;

import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private String review;
    private Long score;
    private Boolean state;
}
