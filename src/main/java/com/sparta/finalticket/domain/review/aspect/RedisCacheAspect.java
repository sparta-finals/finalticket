package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.service.RedisCacheService;
import com.sparta.finalticket.domain.review.service.RedisReviewService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheAspect {

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
                // 캐시에 데이터가 존재하면 반환
                return cachedReviewData;
            } else {
                // 캐시에 데이터가 없으면 메서드 실행
                String reviewData = (String) joinPoint.proceed();

                // 실행 결과를 캐시에 저장
                redisCacheService.cacheReviewData(reviewId, reviewData);

                return reviewData;
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
            String reviewData = (String) joinPoint.proceed();
            return reviewData;
        } else {
            // 캐시된 데이터가 있으면 해당 정보를 이용하여 DTO 생성
            ReviewResponseDto responseDto = new ReviewResponseDto();
            // 아래에서 정보를 설정하는 대신, 생성자를 이용하여 데이터를 전달합니다.
            return responseDto;
        }
    }
}
