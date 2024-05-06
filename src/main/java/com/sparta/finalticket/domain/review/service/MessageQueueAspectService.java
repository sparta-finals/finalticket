package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageQueueAspectService {

    private final ReviewQueueService reviewQueueService;

    @AfterReturning(pointcut = "execution(* com.sparta.finalticket.domain.review.service.ReviewService.createReview(..))", returning = "result")
    public void afterReviewCreation(ReviewResponseDto result) throws Exception {
        reviewQueueService.enqueueReviewTask(result, 1); // 우선순위 1로 설정
    }
}
