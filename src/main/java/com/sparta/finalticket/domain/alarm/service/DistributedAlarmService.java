package com.sparta.finalticket.domain.alarm.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DistributedAlarmService {

    private final RedissonClient redissonClient;

    private static final String ALARM_LOCK_KEY_PREFIX = "alarmLock:";

    public RLock getLock(Long userId) {
        return redissonClient.getFairLock(ALARM_LOCK_KEY_PREFIX + userId);
    }

    public void lock(RLock lock) throws InterruptedException {
        lock.lock();
    }

    public boolean tryLock(RLock lock, long waitTime, long leaseTime) {
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Interrupt 상태를 재설정
            throw new RuntimeException("락을 획득하는 동안 중단되었습니다", e);
        }
    }

    public void unlock(RLock lock) {
        lock.unlock();
    }
}

