package com.sparta.finalticket.domain.review.controller;

import com.sparta.finalticket.domain.review.dto.request.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.request.ReviewUpdateRequestDto;
import com.sparta.finalticket.domain.review.dto.response.*;
import com.sparta.finalticket.domain.review.entity.ReviewSortType;
import com.sparta.finalticket.domain.review.service.ReviewService;
import com.sparta.finalticket.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games/{gameId}")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/review")
    public ResponseEntity<ReviewResponseDto> postReview(@PathVariable(name = "gameId") Long gameId,
                                                        @RequestBody @Valid ReviewRequestDto requestDto,
                                                        HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        ReviewResponseDto reviewResponseDto = reviewService.createReview(gameId, requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponseDto);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ReviewStatsResponseDto> getReviewsAndStatsByGameId(@PathVariable(name = "gameId") Long gameId) {
        List<ReviewGameListResponseDto> reviews = reviewService.getReviewsByGameId(gameId);
        ReviewCountAndAvgResponseDto countAndAvgResponse = reviewService.getReviewsCountAndAvgByGameId(gameId);

        ReviewStatsResponseDto responseDto = new ReviewStatsResponseDto(reviews, countAndAvgResponse);

        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable(name = "gameId") Long gameId,
                                                       @PathVariable(name = "reviewId") Long reviewId) {
        ReviewResponseDto responseDto = reviewService.getReviewByGameId(gameId, reviewId);
        return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping("/review/{reviewId}")
    public ResponseEntity<ReviewUpdateResponseDto> updateReview(@PathVariable(name = "gameId") Long gameId,
                                                                @PathVariable(name = "reviewId") Long reviewId,
                                                                @RequestBody @Valid ReviewUpdateRequestDto requestDto,
                                                                HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        ReviewUpdateResponseDto responseDto = reviewService.updateReview(gameId, reviewId, requestDto, user);
        return ResponseEntity.ok().body(responseDto);
    }

    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable(name = "gameId") Long gameId,
                                             @PathVariable(name = "reviewId") Long reviewId,
                                             HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        reviewService.deleteReview(gameId, reviewId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/review/{reviewId}/likedislike")
    public ResponseEntity<ReviewResponseDto> likeReview(@PathVariable(name = "gameId") Long gameId,
                                                        @PathVariable(name = "reviewId") Long reviewId) {
        ReviewResponseDto responseDto = reviewService.likeReview(reviewId);
        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/review/{reviewId}/dislike")
    public ResponseEntity<ReviewResponseDto> dislikeReview(@PathVariable(name = "gameId") Long gameId,
                                                           @PathVariable(name = "reviewId") Long reviewId) {
        ReviewResponseDto responseDto = reviewService.dislikeReview(reviewId);
        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/review/{reviewId}/report")
    public ResponseEntity<Void> reportReview(@PathVariable(name = "gameId") Long gameId,
                                             @PathVariable(name = "reviewId") Long reviewId,
                                             HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        reviewService.reportReview(gameId, reviewId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews/filter")
    public ResponseEntity<List<ReviewResponseDto>> filterReviewsByCriteria(
            @PathVariable(name = "gameId") Long gameId,
            @RequestParam(name = "minScore", required = false) Long minScore,
            @RequestParam(name = "maxScore", required = false) Long maxScore) {
        List<ReviewResponseDto> filteredReviews = reviewService.filterReviewsByCriteria(gameId, minScore, maxScore);
        return ResponseEntity.ok().body(filteredReviews);
    }

    @PostMapping("/review/{reviewId}/recommend")
    public ResponseEntity<ReviewResponseDto> recommendReview(@PathVariable(name = "gameId") Long gameId,
                                                             @PathVariable(name = "reviewId") Long reviewId) {
        ReviewResponseDto responseDto = reviewService.recommendReview(reviewId);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/reviews/filter/sorted")
    public ResponseEntity<List<ReviewResponseDto>> filterReviewsByCriteria(
            @PathVariable(name = "gameId") Long gameId,
            @RequestParam(name = "minScore", required = false) Long minScore,
            @RequestParam(name = "maxScore", required = false) Long maxScore,
            @RequestParam(name = "sortType", defaultValue = "LATEST") ReviewSortType sortType) { // 기본값으로 최신순 설정
        List<ReviewResponseDto> filteredReviews = reviewService.filterReviewsByCriteria(gameId, minScore, maxScore, sortType);
        return ResponseEntity.ok().body(filteredReviews);
    }

    @GetMapping("/reviews/popular")
    public ResponseEntity<List<PopularReviewResponseDto>> getPopularReviews(@PathVariable(name = "gameId") Long gameId,
                                                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<PopularReviewResponseDto> popularReviews = reviewService.getPopularReviewsByGameId(gameId, page, size);
        return ResponseEntity.ok().body(popularReviews);
    }

    @GetMapping("/reviews/activityHour")
    public ResponseEntity<Map<LocalTime, Long>> getReviewActivityByHour(@PathVariable(name = "gameId") Long gameId) {
        Map<LocalTime, Long> reviewActivityByHour = reviewService.getReviewActivityByHourForGame(gameId);
        return ResponseEntity.ok().body(reviewActivityByHour);
    }

    @GetMapping("/reviews/genre")
    public ResponseEntity<List<ReviewGenreResponseDto>> getReviewsByGenre(
            @PathVariable(name = "gameId") Long gameId) {
        List<ReviewGenreResponseDto> reviews = reviewService.getReviewsByGenre(gameId);
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/reviews/filter/trustScore")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsFilteredByTrustScore(
            @PathVariable(name = "gameId") Long gameId,
            @RequestParam(name = "minScore", required = false) Long minScore,
            @RequestParam(name = "maxScore", required = false) Long maxScore,
            @RequestParam(name = "sortType", defaultValue = "LATEST") ReviewSortType sortType) { // 기본값으로 최신순 설정
        List<ReviewResponseDto> filteredReviews = reviewService.filterReviewsByTrustScore(gameId, minScore, maxScore, sortType);
        return ResponseEntity.ok().body(filteredReviews);
    }

    @GetMapping("/reviews/trustScore")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsSortedByTrustScore(
            @PathVariable(name = "gameId") Long gameId,
            @RequestParam(name = "sortType", defaultValue = "LATEST") ReviewSortType sortType) { // 기본값으로 최신순 설정
        List<ReviewResponseDto> sortedReviews = reviewService.getReviewsWithTrustScore(gameId, sortType);
        return ResponseEntity.ok().body(sortedReviews);
    }

    @GetMapping("/user/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getUserReviews(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<ReviewResponseDto> userReviews = reviewService.getUserReviews(user);
        return ResponseEntity.ok().body(userReviews);
    }

    @GetMapping("/reviews/all")
    public ResponseEntity<List<ReviewListResponseDto>> getAllReviewsByGameId(
            @PathVariable(name = "gameId") Long gameId) {
        List<ReviewListResponseDto> reviews = reviewService.getAllReviewsByGameId(gameId);
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/reviews/analysis")
    public ResponseEntity<ReviewScoreAnalysisResponseDto> analyzeReviewScores(@PathVariable(name = "gameId") Long gameId) {
        ReviewScoreAnalysisResponseDto analysisResponseDto = reviewService.analyzeReviewScores(gameId);
        return ResponseEntity.ok().body(analysisResponseDto);
    }

    @GetMapping("/reviews/filterByDate")
    public ResponseEntity<List<ReviewResponseDto>> filterReviewsByCriteria(
            @PathVariable(name = "gameId") Long gameId,
            @RequestParam(name = "minScore", required = false) Long minScore,
            @RequestParam(name = "maxScore", required = false) Long maxScore,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) {
        List<ReviewResponseDto> filteredReviews = reviewService.retrieveFilteredReviews(gameId, minScore, maxScore, fromDate);
        return ResponseEntity.ok().body(filteredReviews);
    }
}
