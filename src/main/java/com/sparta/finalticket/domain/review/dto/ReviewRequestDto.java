package com.sparta.finalticket.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private String review;
    private Long score;
    private Long userId;
    private Long gameId;
}
