package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.dto.response.ReviewAspectResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCachingAspect {

    private final RedisCacheService redisCacheService;

    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..)) " +
        "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.createReview(..)) " +
        "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.updateReview(..)) " +
        "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.deleteReview(..))")
    public void reviewPointcut() {
    }

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
                return new ReviewAspectResponseDto(cachedReviewData);
            } else {
                // 캐시에 데이터가 없으면 메서드 실행
                Review reviewData = (Review) joinPoint.proceed();

                // 실행 결과를 캐시에 저장
                redisCacheService.cacheReviewData(reviewId, reviewData);

                return new ReviewAspectResponseDto(reviewData);
            }
        } else {
            // 리뷰 생성, 수정, 삭제 메서드의 경우 gameId가 첫 번째 매개변수로 전달됨
            gameId = (Long) args[0];

            // 리뷰 작성, 수정, 삭제 메서드 실행
            result = joinPoint.proceed();

            // 메서드 실행 후에 캐시를 갱신하여 최신 데이터를 캐싱함
            redisCacheService.clearAllReviews();

            return result;
        }
    }
}
