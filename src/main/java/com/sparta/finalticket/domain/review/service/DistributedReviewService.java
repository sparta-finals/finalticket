package com.sparta.finalticket.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DistributedReviewService {

    private final RedissonClient redissonClient;

    private static final String REVIEW_LOCK_KEY_PREFIX = "reviewLock:";

    public RLock getLock(Long alarmId) {
        return redissonClient.getFairLock(REVIEW_LOCK_KEY_PREFIX + alarmId);
    }

    public boolean tryLock(RLock lock, long waitTime, long leaseTime) throws InterruptedException {
        return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
    }

    public void unlock(RLock lock) {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
