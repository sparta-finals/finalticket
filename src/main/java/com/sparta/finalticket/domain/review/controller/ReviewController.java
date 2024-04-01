package com.sparta.finalticket.domain.review.controller;

import com.sparta.finalticket.domain.review.dto.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.ReviewResponseDto;
import com.sparta.finalticket.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{id}/review")
    public ResponseEntity<Void> createReview(
        @PathVariable Long id,
        @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.createReview(id, reviewRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long id) {
        ReviewResponseDto reviewResponseDto = reviewService.getReviewByGameId(id);
        return ResponseEntity.ok().body(reviewResponseDto);
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<ReviewResponseDto> updateReview(
        @PathVariable Long id,
        @RequestBody ReviewRequestDto reviewRequestDto) {
        ReviewResponseDto reviewResponseDto = reviewService.updateReview(id, reviewRequestDto);
        return ResponseEntity.ok().body(reviewResponseDto);
    }

    @DeleteMapping("/{id}/review")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
