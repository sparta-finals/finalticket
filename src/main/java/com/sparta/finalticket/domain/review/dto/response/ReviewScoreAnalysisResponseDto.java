package com.sparta.finalticket.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewScoreAnalysisResponseDto {

    private Long maxScore;
    private Long minScore;
    private Double averageScore;
}
