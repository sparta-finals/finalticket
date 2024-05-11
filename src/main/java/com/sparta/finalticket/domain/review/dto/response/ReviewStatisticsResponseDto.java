package com.sparta.finalticket.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatisticsResponseDto {

    private Long totalReviewCount;
    private Double averageReviewScore;
    private Long positiveReviewCount;
    private Long negativeReviewCount;
}
