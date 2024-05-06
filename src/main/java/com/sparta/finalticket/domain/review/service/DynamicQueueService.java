package com.sparta.finalticket.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DynamicQueueService {

    private final RabbitAdmin rabbitAdmin;

    public void createQueue(String queueName) {
        Queue queue = new Queue(queueName);
        rabbitAdmin.declareQueue(queue);
        System.out.println("Queue created: " + queueName);
    }

    public void updateQueue(String queueName, Queue newQueue) {
        rabbitAdmin.deleteQueue(queueName); // 기존 큐 삭제
        rabbitAdmin.declareQueue(newQueue); // 새로운 큐 생성
        System.out.println("Queue updated: " + queueName);
    }

    public void deleteQueue(String queueName) {
        rabbitAdmin.deleteQueue(queueName);
        System.out.println("Queue deleted: " + queueName);
    }
}
