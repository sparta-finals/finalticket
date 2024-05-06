package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MessageQueueAspectService {

    private final ReviewQueueService reviewQueueService;

    @AfterReturning(pointcut = "execution(* com.sparta.finalticket.domain.review.service.ReviewService.createReview(..))", returning = "result")
    public void afterReviewCreation(ReviewResponseDto result) {
        try {
            reviewQueueService.enqueueReviewTask(result, 1); // 우선순위 1로 설정
            reviewQueueService.enqueueBatchReviewTasks(Arrays.asList(result), 2);
        } catch (Exception e) {
            // 리뷰 작업을 큐에 추가하는 중에 예외가 발생하면 적절히 처리한다.
            System.err.println("리뷰 작업을 큐에 추가하는 중에 예외가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            // 예외 처리 방법에 따라 적절한 조치를 취한다.
        }
    }
}
