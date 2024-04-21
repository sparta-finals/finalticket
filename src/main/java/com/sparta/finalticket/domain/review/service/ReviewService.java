package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.review.dto.request.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.request.ReviewUpdateRequestDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewGameListResponseDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.dto.response.ReviewUpdateResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.global.exception.review.GameIdRequiredException;
import com.sparta.finalticket.global.exception.review.ReviewGameNotFoundException;
import com.sparta.finalticket.global.exception.review.ReviewNotFoundException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final RedisCacheService redisCacheService;
    private final ReviewStatisticService reviewStatisticService;
    private final DistributedReviewService distributedReviewService;

    @Transactional
    public ReviewResponseDto createReview(Long gameId, ReviewRequestDto requestDto, User user) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                Review review = createReviewFromRequest(gameId, requestDto);
                review.setUser(user);
                Review createdReview = reviewRepository.save(review);
                createCacheAndRedis(gameId, createdReview);
                reviewStatisticService.updateReviewStatistics(gameId);
                return new ReviewResponseDto(createdReview);
            } else {
                throw new RuntimeException("리뷰 생성을 위한 락 획득에 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReviewNotFoundException("리뷰 생성을 위해 락을 획득하는 도중에 중단되었습니다.");
        } finally {
            distributedReviewService.unlock(lock);
        }
    }

    @Transactional(readOnly = true)
    public List<ReviewGameListResponseDto> getReviewsByGameId(Long gameId) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                List<Review> reviews = reviewRepository.findByGameId(gameId);
                return reviews.stream()
                    .map(ReviewGameListResponseDto::new)
                    .toList();
            } else {
                throw new ReviewNotFoundException("게임 ID에 대한 리뷰 조회를 위한 락 획득에 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReviewNotFoundException("게임 ID에 대한 리뷰 조회를 위해 락을 획득하는 도중에 중단되었습니다.");
        } finally {
            distributedReviewService.unlock(lock);
        }
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewByGameId(Long gameId, Long reviewId) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                Review review = getReviewByIdAndGameId(gameId, reviewId);
                Game game = getGameById(gameId);
                review.setGame(game);
                getCacheAndRedis(reviewId, review);
                return new ReviewResponseDto(review);
            } else {
                throw new ReviewNotFoundException("경기 ID에 대한 리뷰 조회를 위한 락 획득에 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReviewNotFoundException("경기 ID에 대한 리뷰 조회를 위해 락을 획득하는 도중에 중단되었습니다.");
        } finally {
            distributedReviewService.unlock(lock);
        }
    }

    @Transactional
    public ReviewUpdateResponseDto updateReview(Long gameId, Long reviewId, ReviewUpdateRequestDto requestDto, User user) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                Review review = updateReviewFromRequest(gameId, reviewId, requestDto);
                review.setUser(user);
                Review updatedReview = reviewRepository.save(review);
                updateCacheAndRedis(reviewId, updatedReview);
                reviewStatisticService.updateReviewStatistics(gameId);
                return new ReviewUpdateResponseDto(updatedReview);
            } else {
                throw new ReviewNotFoundException("리뷰 업데이트를 위한 락 획득에 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReviewNotFoundException("리뷰 업데이트를 위해 락을 획득하는 도중에 중단되었습니다.");
        } finally {
            distributedReviewService.unlock(lock);
        }
    }

    @Transactional
    public void deleteReview(Long gameId, Long reviewId, User user) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                Review review = deleteReviewById(reviewId);
                Game game = new Game();
                review.setGame(game);
                reviewRepository.delete(review);
                deleteCacheAndRedis(reviewId);
                reviewStatisticService.updateReviewStatistics(gameId);
            } else {
                throw new ReviewNotFoundException("리뷰 삭제를 위한 락 획득에 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReviewNotFoundException("리뷰 삭제를 위해 락을 획득하는 도중에 중단되었습니다.");
        } finally {
            distributedReviewService.unlock(lock);
        }
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

    public void createCacheAndRedis(Long gameId, Review review) {
        redisCacheService.createReview(review.getId(), review);
    }

    public void getCacheAndRedis(Long reviewId, Review review) {
        redisCacheService.getReview(reviewId, review);
    }

    public void updateCacheAndRedis(Long reviewId, Review review) {
        redisCacheService.updateReview(reviewId, review);
    }

    public void deleteCacheAndRedis(Long reviewId) {
        redisCacheService.clearReviewCache(reviewId);
    }

    private Game getGameById(Long gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new ReviewGameNotFoundException("경기를 찾을 수 없습니다."));
    }

    private Review getReviewByIdAndGameId(Long gameId, Long reviewId) {
        return (Review) reviewRepository.findReviewByGameIdAndReviewId(gameId, reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
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
