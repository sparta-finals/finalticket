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

    public RLock getLock(Long alarmId) {
        return redissonClient.getFairLock(ALARM_LOCK_KEY_PREFIX + alarmId);
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
