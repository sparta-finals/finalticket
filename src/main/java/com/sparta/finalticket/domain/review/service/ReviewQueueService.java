package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewQueueService {

    private final RabbitTemplate rabbitTemplate;
    private final Queue reviewQueue;

    @Transactional(rollbackFor = Exception.class)
    public void enqueueReviewTask(Object reviewTask, int priority) {
        try {
            rabbitTemplate.convertAndSend(reviewQueue.getName(), reviewTask, message -> {
                message.getMessageProperties().setPriority(priority);
                return message;
            });
            System.out.println("우선순위 " + priority + "로 리뷰 작업을 성공적으로 큐에 추가했습니다.");
        } catch (Exception e) {
            // 메시지 큐에 메시지를 보낼 때 예외가 발생하면 적절히 처리한다.
            System.err.println("리뷰 작업을 큐에 추가하는 중에 예외가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            // 예외 처리 방법에 따라 적절한 조치를 취한다.
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void enqueueBatchReviewTasks(List<Object> reviewTasks, int priority) {
        reviewTasks.stream()
                .forEach(reviewTask -> enqueueReviewTask(reviewTask, priority));
        System.out.println("우선순위 " + priority + "로 일괄 리뷰 작업을 성공적으로 큐에 추가했습니다.");
    }

}



