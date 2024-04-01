package com.sparta.finalticket.domain.review.controller;

import com.sparta.finalticket.domain.review.dto.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.ReviewResponseDto;
import com.sparta.finalticket.domain.review.service.ReviewService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
        @Valid @PathVariable Long id,
        @RequestBody ReviewRequestDto reviewRequestDto,
        HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        reviewService.createReview(id, reviewRequestDto, user.getId());
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
        @RequestBody ReviewRequestDto reviewRequestDto,
        HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        ReviewResponseDto reviewResponseDto = reviewService.updateReview(id, reviewRequestDto, user.getId());
        return ResponseEntity.ok().body(reviewResponseDto);
    }

    @DeleteMapping("/{id}/review")
    public ResponseEntity<Void> deleteReview(
        @PathVariable Long id,
        HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        reviewService.deleteReview(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
