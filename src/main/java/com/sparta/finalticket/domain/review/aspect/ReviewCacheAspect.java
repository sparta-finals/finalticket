package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.service.RedisCacheService;
import com.sparta.finalticket.domain.review.service.RedisReviewService;
import com.sparta.finalticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewCacheAspect {

    private final RedisCacheService redisCacheService;
    private final RedisReviewService redisReviewService;

    // 리뷰 조회 메서드와 리뷰 생성, 수정, 삭제 메서드에 대한 포인트컷 설정
    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..)) " +
        "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.createReview(..)) " +
        "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.updateReview(..)) " +
        "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.deleteReview(..))")
    public void reviewPointcut() {}

    // Around 어드바이스 수정: 리뷰 조회 및 리뷰 작성, 수정, 삭제 메서드 실행 전후로 캐싱 로직을 적용
    @Around("reviewPointcut()")
    public Object cacheReviewDataForReviewService(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        Object[] args = joinPoint.getArgs();
        Long gameId;

        // 리뷰 조회 메서드의 경우 gameId가 두 번째 매개변수로 전달됨
        if (joinPoint.getSignature().getName().equals("getReviewByGameId")) {
            gameId = (Long) args[0];
            Long reviewId = (Long) args[1];

            // 캐시에서 리뷰 데이터 가져오기
            String cachedReviewData = redisCacheService.getCachedReviewData(reviewId);

            if (cachedReviewData != null) {
                // 캐시에 데이터가 존재하면 ReviewResponseDto를 생성하여 반환
                return new ReviewResponseDto(parseCachedReviewData(cachedReviewData));
            } else {
                // 캐시에 데이터가 없으면 메서드 실행
                result = joinPoint.proceed();

                // 실행 결과를 캐시에 저장
                redisCacheService.cacheReviewData(reviewId, (ReviewResponseDto) result);

                return result;
            }
        } else {
            // 리뷰 생성, 수정, 삭제 메서드의 경우 gameId가 첫 번째 매개변수로 전달됨
            gameId = (Long) args[0];

            // 메서드 실행 전에 캐시를 삭제하여 업데이트된 내용이 반영될 수 있도록 함
            redisCacheService.clearReviewCache();

            // 리뷰 작성, 수정, 삭제 메서드 실행
            result = joinPoint.proceed();

            // 메서드 실행 후에 캐시를 갱신하여 최신 데이터를 캐싱함
            redisCacheService.clearAllReviews();

            return result;
        }
    }

    @Around("execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..))")
    public Object cacheReviewDataForRedisService(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long gameId = (Long) args[0];
        Long reviewId = (Long) args[1];

        // Redis에서 총 리뷰 수와 평균 점수 가져오기
        Long totalReviewCount = redisReviewService.getTotalReviewCount(gameId);
        Double averageReviewScore = redisReviewService.getAverageReviewScore(gameId);

        // 캐시된 데이터가 없으면 메서드 실행
        if (totalReviewCount == 0 || averageReviewScore == 0.0) {
            return joinPoint.proceed();
        } else {
            String cachedReviewData = redisCacheService.getCachedReviewData(reviewId);
            if (cachedReviewData != null) {
                return new ReviewResponseDto(parseCachedReviewData(cachedReviewData));
            } else {
                return createDefaultReviewResponse();
            }
        }
    }

    private ReviewResponseDto parseCachedReviewData(String cachedReviewData) {
        String[] parts = cachedReviewData.split(",");
        Long id = Long.parseLong(parts[0]);
        String review = parts[1];
        Long score = Long.parseLong(parts[2]);
        Boolean state = Boolean.parseBoolean(parts[3]);
        Long userId = Long.parseLong(parts[4]);
        Long gameId = Long.parseLong(parts[5]);
        return new ReviewResponseDto(id, review, score, state, userId, gameId);
    }

    private ReviewResponseDto createDefaultReviewResponse() {
        Review defaultReview = new Review();
        defaultReview.setId(-1L);
        defaultReview.setReview("No review available");
        defaultReview.setScore(0L);
        defaultReview.setState(false);

        User defaultUser = new User();
        defaultUser.setId(-1L);

        Game defaultGame = new Game();
        defaultGame.setId(-1L);

        defaultReview.setUser(defaultUser);
        defaultReview.setGame(defaultGame);

        return new ReviewResponseDto(defaultReview);
    }

}
