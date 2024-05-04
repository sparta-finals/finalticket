package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.review.dto.request.ReviewRequestDto;
import com.sparta.finalticket.domain.review.dto.request.ReviewUpdateRequestDto;
import com.sparta.finalticket.domain.review.dto.response.*;
import com.sparta.finalticket.domain.review.entity.Genre;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.entity.ReviewSortType;
import com.sparta.finalticket.domain.review.repository.ReviewRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.global.exception.review.GameIdRequiredException;
import com.sparta.finalticket.global.exception.review.ReviewGameNotFoundException;
import com.sparta.finalticket.global.exception.review.ReviewNotFoundException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final RedisCacheService redisCacheService;
    private final ReviewStatisticService reviewStatisticService;
    private final DistributedReviewService distributedReviewService;
    private final RedisReviewService redisReviewService;
    private final RealTimeReviewUpdateService realTimeReviewUpdateService;

    @Transactional
    public ReviewResponseDto createReview(Long gameId, ReviewRequestDto requestDto, User user) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                Review review = createReviewFromRequest(gameId, requestDto);
                review.setUser(user);
                review.setReviewTime(LocalDateTime.now()); // 리뷰 작성 시간 저장
                review.setUserTrustScore(requestDto.getUserTrustScore());
                Review createdReview = reviewRepository.save(review);
                createCacheAndRedis(gameId, createdReview);
                reviewStatisticService.updateReviewStatistics(gameId);
                // 실시간 리뷰 업데이트 기능 호출
                realTimeReviewUpdateService.updateReviewAndNotify(gameId, createdReview);
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
                throw new ReviewNotFoundException("경기 ID에 대한 리뷰 조회를 위한 락 획득에 실패했습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReviewNotFoundException("경기 ID에 대한 리뷰 조회를 위해 락을 획득하는 도중에 중단되었습니다.");
        } finally {
            distributedReviewService.unlock(lock);
        }
    }

    @Transactional(readOnly = true)
    public ReviewCountAndAvgResponseDto getReviewsCountAndAvgByGameId(Long gameId) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                Double avg = Math.round(redisReviewService.getAverageReviewScore(gameId) * 10) / 10.0;
                Long count = redisReviewService.getTotalReviewCount(gameId);
                return new ReviewCountAndAvgResponseDto(avg, count);
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

    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewByGameId(Long gameId, Long reviewId) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                Review review = ReviewByIdAndGameId(gameId, reviewId);
                Game game = GameById(gameId);
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
                // 실시간 리뷰 업데이트 기능 호출
                realTimeReviewUpdateService.updateReviewAndNotify(gameId, updatedReview);
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
                review.setUser(user);
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

    @Transactional
    public ReviewResponseDto likeReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        Long likeCount = review.getLikeCount();
        if (likeCount == null) {
            review.setLikeCount(1L);
        } else {
            review.setLikeCount(likeCount + 1);
        }
        Review updatedReview = reviewRepository.save(review);
        return new ReviewResponseDto(updatedReview);
    }

    @Transactional
    public ReviewResponseDto dislikeReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        if (review.getDislikeCount() > 0) {
            review.setDislikeCount(review.getDislikeCount() - 1);
            Review updatedReview = reviewRepository.save(review);
            return new ReviewResponseDto(updatedReview);
        } else {
            throw new ReviewNotFoundException("이 리뷰에 대한 싫어요 수는 이미 0입니다.");
        }
    }

    @Transactional
    public void reportReview(Long gameId, Long reviewId, User user) {
        Review review = getReviewById(reviewId);
        review.setReported(true);
        Long reportCount = review.getReportCount();
        review.setReportCount(reportCount != null ? reportCount + 1 : 1);
        reviewRepository.save(review);
    }

    public List<ReviewResponseDto> filterReviewsByCriteria(Long gameId, Long minScore, Long maxScore) {
        List<Review> reviews = reviewRepository.findByGameId(gameId);

        // 평점 기준으로 필터링 및 정렬
        List<Review> filteredAndSortedReviews = reviews.stream()
                .filter(review -> (minScore == null || review.getScore() >= minScore) &&
                        (maxScore == null || review.getScore() <= maxScore))
                .sorted(Comparator.comparingLong(Review::getScore)) // 평점에 따라 오름차순 정렬
                .toList();

        return filteredAndSortedReviews.stream()
                .map(ReviewResponseDto::new)
                .toList();
    }

    @Transactional
    public ReviewResponseDto recommendReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        Long recommendationCount = review.getRecommendationCount();
        if (recommendationCount == null) {
            review.setRecommendationCount(1L);
        } else {
            review.setRecommendationCount(recommendationCount + 1);
        }
        Review updatedReview = reviewRepository.save(review);
        return new ReviewResponseDto(updatedReview);
    }

    // 모든 게임의 리뷰 통계를 업데이트하는 메서드
    @Transactional
    public void updateReviewStatisticsForAllGames() {
        List<Game> games = gameRepository.findAll();
        games.forEach(game -> reviewStatisticService.updateReviewStatistics(game.getId()));
    }

    // 모든 리뷰 데이터를 조회하는 메서드
    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> filterReviewsByCriteria(Long gameId, Long minScore, Long maxScore, ReviewSortType sortType) {
        List<Review> reviews = reviewRepository.findByGameId(gameId);

        // 평점 기준으로 필터링
        List<Review> filteredReviews = reviews.stream()
                .filter(review -> (minScore == null || review.getScore() >= minScore) &&
                        (maxScore == null || review.getScore() <= maxScore))
                .toList();

        // 선택된 정렬 방법에 해당하는 Comparator 인스턴스를 가져옴
        Comparator<Review> comparator = sortType.getComparator();

        // 정렬
        List<Review> sortedReviews = filteredReviews.stream()
                .sorted(comparator)
                .toList();

        // DTO로 변환하여 반환
        return sortedReviews.stream()
                .map(ReviewResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PopularReviewResponseDto> getPopularReviewsByGameId(Long gameId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("viewCount").descending());
        List<Review> popularReviews = reviewRepository.findTopPopularReviewsByGameId(gameId, pageable);
        return popularReviews.stream()
                .map(PopularReviewResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<LocalTime, Long> getReviewActivityByHourForGame(Long gameId) {
        List<Review> reviews = reviewRepository.findByGameId(gameId); // 해당 게임의 모든 리뷰를 가져옴
        return trackReviewActivityByHour(reviews); // 시간대별 리뷰 활동을 추적하여 반환
    }

    public Map<LocalTime, Long> trackReviewActivityByHour(List<Review> reviews) {
        // 리뷰가 작성된 시간대별로 카운트를 추적할 Map
        Map<LocalTime, Long> reviewActivityByHour = new HashMap<>();

        // 모든 리뷰를 시간대별로 그룹화하여 카운트
        reviews.stream()
                .map(review -> review.getCreatedAt().toLocalTime()) // 리뷰 작성 시간대 추출
                .collect(Collectors.groupingBy(time -> time, counting())) // 시간대별로 그룹화하여 카운트
                .forEach(reviewActivityByHour::put); // 결과를 Map에 저장

        return reviewActivityByHour;
    }

    public List<ReviewGenreResponseDto> getReviewsByGenre(Long gameId) {
        RLock lock = distributedReviewService.getLock(gameId);
        try {
            if (distributedReviewService.tryLock(lock, 1000, 5000)) {
                List<Review> reviews = reviewRepository.findGameById(gameId);
                return reviews.stream()
                        .map(ReviewGenreResponseDto::new)
                        .toList();
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

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> filterReviewsByTrustScore(Long gameId, Long minScore, Long maxScore, ReviewSortType sortType) {
        List<Review> reviews = reviewRepository.findByGameId(gameId);

        // 평점 기준으로 필터링
        List<Review> filteredReviews = reviews.stream()
                .filter(review -> (minScore == null || review.getScore() >= minScore) &&
                        (maxScore == null || review.getScore() <= maxScore))
                .toList();

        // 선택된 정렬 방법에 해당하는 Comparator 인스턴스를 가져옴
        Comparator<Review> comparator = sortType.getComparator();

        // 정렬
        List<Review> sortedReviews = filteredReviews.stream()
                .sorted(comparator)
                .toList();

        // DTO로 변환하여 반환
        return sortedReviews.stream()
                .map(ReviewResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsWithTrustScore(Long gameId, ReviewSortType sortType) {
        if (gameId == null) {
            throw new IllegalArgumentException("경기 ID는 null일 수 없습니다.");
        }

        Optional<Review> reviews = reviewRepository.findById(gameId);

        if (reviews == null || reviews.isEmpty()) {
            return Collections.emptyList();
        }

        // 리뷰 작성자의 신뢰도를 기준으로 정렬
        Comparator<Review> comparator = Comparator.comparingDouble(Review::getUserTrustScore).reversed();

        // 선택된 정렬 방법에 따라 정렬
        if (sortType == ReviewSortType.LATEST) {
            comparator = comparator.thenComparing(Comparator.comparing(Review::getCreatedAt).reversed());
        } else if (sortType == ReviewSortType.HIGHEST_SCORE) {
            comparator = comparator.thenComparingLong(Review::getViewCount).reversed();
        }

        // 정렬
        List<Review> sortedReviews = reviews.stream()
                .sorted(comparator)
                .toList();

        // DTO로 변환하여 반환
        return sortedReviews.stream()
                .map(ReviewResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getUserReviews(User user) {
        List<Review> userReviews = reviewRepository.findByUser(user);
        return userReviews.stream()
                .map(ReviewResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponseDto> getAllReviewsByGameId(Long gameId) {
        List<Review> reviews = reviewRepository.findByGameId(gameId);
        return reviews.stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed()) // 최신순으로 정렬
                .map(ReviewListResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewScoreAnalysisResponseDto analyzeReviewScores(Long gameId) {
        List<Review> reviews = reviewRepository.findByGameId(gameId);

        // 모든 리뷰의 점수를 추출하여 리스트로 변환
        List<Long> scores = reviews.stream()
                .map(Review::getScore)
                .sorted(Comparator.reverseOrder()) // 평점을 내림차순으로 정렬
                .toList();

        // 최고 평점 및 최저 평점을 구함
        Long maxScore = scores.isEmpty() ? 0L : scores.get(0); // 정렬된 리스트에서 첫 번째 요소가 최고 평점
        Long minScore = scores.isEmpty() ? 0L : scores.get(scores.size() - 1); // 정렬된 리스트에서 마지막 요소가 최저 평점

        // 평균 평점을 계산
        Double averageScore = scores.stream()
                .mapToDouble(Long::doubleValue)
                .average()
                .orElse(0.0);

        // 리뷰 점수 분석 및 비교 결과를 DTO로 반환
        return new ReviewScoreAnalysisResponseDto(maxScore, minScore, averageScore);
    }

    private Review createReviewFromRequest(Long gameId, ReviewRequestDto requestDto) {
        if (gameId == null) {
            throw new GameIdRequiredException("게임 ID가 필요합니다.");
        }

        Review review = new Review();
        review.setReview(requestDto.getReview());
        review.setScore(requestDto.getScore());
        review.setState(true);
        review.setGenre(requestDto.getGenre());
        Game game = GameById(gameId);
        review.setGame(game);
        return review;
    }

    private Review updateReviewFromRequest(Long gameId, Long reviewId, ReviewUpdateRequestDto requestDto) {
        Review review = updateReviewById(reviewId);
        Game game = GameById(gameId);
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

    private Game GameById(Long gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new ReviewGameNotFoundException("경기를 찾을 수 없습니다."));
    }

    private Review ReviewByIdAndGameId(Long gameId, Long reviewId) {
        return (Review) reviewRepository.findReviewByGameIdAndReviewId(gameId, reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
    }

    Review getReviewById(Long reviewId) {
        return reviewRepository.findReviewByIdAndStateTrue(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
    }

    private Review updateReviewById(Long reviewId) {
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
