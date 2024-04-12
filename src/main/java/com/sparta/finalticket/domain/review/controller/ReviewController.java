package com.sparta.finalticket.domain.review.controller;

import com.sparta.finalticket.domain.review.dto.request.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.request.ReviewUpdateRequestDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewUpdateResponseDto;
import com.sparta.finalticket.domain.review.service.ReviewService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games/{gameId}/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> postReview(@PathVariable(name = "gameId") Long gameId,
                                                        @RequestBody @Valid ReviewRequestDto requestDto,
                                                        HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        ReviewResponseDto reviewResponseDto = reviewService.createReview(gameId, requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponseDto);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable(name = "gameId") Long gameId,
                                                       @PathVariable(name = "reviewId") Long reviewId) {
        ReviewResponseDto responseDto = reviewService.getReviewByGameId(gameId, reviewId);
        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewUpdateResponseDto> updateReview(@PathVariable(name = "gameId") Long gameId,
                                                                @PathVariable(name = "reviewId") Long reviewId,
                                                                @RequestBody @Valid ReviewUpdateRequestDto requestDto,
                                                                HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        ReviewUpdateResponseDto responseDto = reviewService.updateReview(gameId, reviewId, requestDto, user);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable(name = "gameId") Long gameId,
                                             @PathVariable(name = "reviewId") Long reviewId,
                                             HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        reviewService.deleteReview(gameId, reviewId, user);
        return ResponseEntity.noContent().build();
    }
}
