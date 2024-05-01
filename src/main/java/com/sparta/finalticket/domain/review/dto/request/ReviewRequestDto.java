package com.sparta.finalticket.domain.review.dto.request;

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

    private Long likeCount;

    private Long dislikeCount;
}
