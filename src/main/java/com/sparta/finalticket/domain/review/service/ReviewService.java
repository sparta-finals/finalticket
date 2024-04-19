package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.review.dto.request.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.request.ReviewUpdateRequestDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewUpdateResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.global.exception.review.GameIdRequiredException;
import com.sparta.finalticket.global.exception.review.ReviewGameNotFoundException;
import com.sparta.finalticket.global.exception.review.ReviewNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final RedisCacheService redisCacheService;
    private final RedisReviewService redisReviewService;

    @Transactional
    public ReviewResponseDto createReview(Long gameId, ReviewRequestDto requestDto, User user) {
        Review review = createReviewFromRequest(gameId, requestDto);
        review.setUser(user);
        Review createdReview = reviewRepository.save(review);
        createCacheAndRedis(gameId, createdReview);
        return new ReviewResponseDto(createdReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewByGameId(Long gameId, Long reviewId) {
        String cachedReviewData = redisCacheService.getCachedReviewData(reviewId);
        if (cachedReviewData != null) {
            return new ReviewResponseDto(cachedReviewData);
        } else {
            Review review = getReviewById(reviewId);
            Game game = getGameById(gameId);
            review.setGame(game);
            ReviewResponseDto responseDto = new ReviewResponseDto(review);
            redisCacheService.cacheReviewData(review.getId(), responseDto.toString());
            return responseDto;
        }
    }

    @Transactional
    public ReviewUpdateResponseDto updateReview(Long gameId, Long reviewId, ReviewUpdateRequestDto requestDto, User user) {
        Review review = updateReviewFromRequest(gameId, reviewId, requestDto);
        review.setUser(user);
        Review updatedReview = reviewRepository.save(review);
        updateCacheAndRedis(gameId, updatedReview);
        return new ReviewUpdateResponseDto(updatedReview);
    }

    @Transactional
    public void deleteReview(Long gameId, Long reviewId, User user) {
        Review review = deleteReviewById(reviewId);
        Game game = new Game();
        review.setGame(game);
        reviewRepository.delete(review);
        deleteCacheAndRedis(gameId, review);
    }

    private Review createReviewFromRequest(Long gameId, ReviewRequestDto requestDto) {
        if (gameId == null) {
            throw new GameIdRequiredException("게임 ID가 필요합니다.");
        }

        Review review = new Review();
        review.setReview(requestDto.getReview());
        review.setScore(requestDto.getScore());
        review.setState(true);
        Game game = getGameById(gameId);
        review.setGame(game);
        return review;
    }

    private Review updateReviewFromRequest(Long gameId, Long reviewId, ReviewUpdateRequestDto requestDto) {
        Review review = getReviewById(reviewId);
        Game game = getGameById(gameId);
        review.setGame(game);
        review.setReview(requestDto.getReview());
        review.setScore(requestDto.getScore());
        review.setState(true);
        return review;
    }

    private void createCacheAndRedis(Long gameId, Review review) {
        redisCacheService.clearGameCache(gameId);
        redisCacheService.createReview(review.getId(), review.getReview());
        updateRedisStats(gameId);
    }

    private void updateCacheAndRedis(Long gameId, Review review) {
        redisCacheService.clearGameCache(gameId);
        redisCacheService.updateReview(review.getId(), review.getReview());
        updateRedisStats(gameId);
    }

    private void deleteCacheAndRedis(Long gameId, Review review) {
        redisCacheService.clearGameCache(gameId);
        redisCacheService.clearReviewCache(review.getId());
        updateRedisStats(gameId);
    }

    private void updateRedisStats(Long gameId) {
        Long totalReviewCount = reviewRepository.countByGameId(gameId);
        redisReviewService.setTotalReviewCount(gameId, totalReviewCount);

        Double averageReviewScore = reviewRepository.calculateAverageScoreByGameId(gameId);
        redisReviewService.setAverageReviewScore(gameId, averageReviewScore);
    }

    private Game getGameById(Long gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new ReviewGameNotFoundException("경기를 찾을 수 없습니다."));
    }

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findReviewByIdAndStateTrue(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
    }

    private Review deleteReviewById(Long reviewId) {
        return reviewRepository.findReviewByIdAndDeleteId(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰가 존재하지 않습니다."));
    }

    public List<ReviewResponseDto> getUserReviewList(User user) {
        return reviewRepository.getUserReviewList(user);
    }
}
