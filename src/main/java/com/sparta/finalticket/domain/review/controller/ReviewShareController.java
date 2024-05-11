package com.sparta.finalticket.domain.review.controller;

import com.sparta.finalticket.domain.review.dto.request.ReviewShareRequestDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewShareResponseDto;
import com.sparta.finalticket.domain.review.service.ReviewShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/games/{gameId}/review/{reviewId}/share")
@RequiredArgsConstructor
public class ReviewShareController {

    private final ReviewShareService reviewShareService;

    @PostMapping
    public ResponseEntity<ReviewShareResponseDto> shareReview(
            @PathVariable(name = "gameId") Long gameId,
            @PathVariable(name = "reviewId") Long reviewId,
            @RequestBody ReviewShareRequestDto requestDto) {
        ReviewShareResponseDto responseDto = reviewShareService.shareReview(gameId, reviewId, requestDto);
        return ResponseEntity.ok().body(responseDto);
    }
}
