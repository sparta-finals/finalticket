package com.sparta.finalticket.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsResponseDto {
    private List<ReviewGameListResponseDto> reviews;
    private ReviewCountAndAvgResponseDto countAndAvgResponse;
}