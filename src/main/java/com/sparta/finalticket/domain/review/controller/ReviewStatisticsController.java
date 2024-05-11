package com.sparta.finalticket.domain.review.controller;

import com.sparta.finalticket.domain.review.dto.response.ReviewStatisticsResponseDto;
import com.sparta.finalticket.domain.review.service.ReviewStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games/{gameId}/reviews/statistics")
public class ReviewStatisticsController {

    private final ReviewStatisticsService reviewStatisticsService;

    @GetMapping
    public ResponseEntity<ReviewStatisticsResponseDto> getReviewStatistics(@PathVariable(name = "gameId") Long gameId) {
        ReviewStatisticsResponseDto statisticsResponseDto = reviewStatisticsService.getReviewStatistics(gameId);
        return ResponseEntity.ok().body(statisticsResponseDto);
    }
}
