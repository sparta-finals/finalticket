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

    // 데드락 방지를 위해 락을 얻는 순서를 일관되게 유지하고, 모든 필요한 락을 한 번에 획득하는 메서드
    public RLock[] getLocksForReviewOperations(Long reviewId) {
        RLock lock1 = redissonClient.getLock(REVIEW_LOCK_KEY_PREFIX + reviewId + ":operation1");
        RLock lock2 = redissonClient.getLock(REVIEW_LOCK_KEY_PREFIX + reviewId + ":operation2");
        // 다른 리뷰 작업에 필요한 락이 있다면 여기에 추가
        return new RLock[]{lock1, lock2};
    }

    // 낙관적 락을 이용하여 리뷰 작업의 충돌을 방지하는 메서드
    public boolean checkOptimisticLock(Long gameId, long expectedVersion) {
        long actualVersion = redissonClient.getAtomicLong("reviewVersion:" + gameId).get();
        return actualVersion == expectedVersion;
    }

    // try-with-resources 문을 사용하여 자동으로 락을 해제하는 메서드
    public void executeWithLocks(RLock[] locks, Runnable task) {
        try {
            for (RLock lock : locks) {
                lock.lock();
            }
            task.run();
        } finally {
            for (RLock lock : locks) {
                lock.unlock();
            }
        }
    }
}
