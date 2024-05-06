package com.sparta.finalticket.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewQueueService {

    private final RabbitTemplate rabbitTemplate;
    private final Queue reviewQueue;

    @Transactional(rollbackFor = Exception.class)
    public void enqueueReviewTask(Object reviewTask, int priority) throws Exception {
        rabbitTemplate.convertAndSend(reviewQueue.getName(), reviewTask, message -> {
            message.getMessageProperties().setPriority(priority);
            return message;
        });
        System.out.println("우선순위 " + priority + "로 리뷰 작업을 성공적으로 큐에 추가했습니다.");
    }

    @Transactional(rollbackFor = Exception.class)
    public void enqueueBatchReviewTasks(Object[] reviewTasks, int priority) throws Exception {
        for (Object reviewTask : reviewTasks) {
            rabbitTemplate.convertAndSend(reviewQueue.getName(), reviewTask, message -> {
                message.getMessageProperties().setPriority(priority);
                return message;
            });
        }
        System.out.println("우선순위 " + priority + "로 일괄 리뷰 작업을 성공적으로 큐에 추가했습니다.");
    }
}

