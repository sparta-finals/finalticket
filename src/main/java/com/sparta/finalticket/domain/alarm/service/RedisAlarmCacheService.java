package com.sparta.finalticket.domain.alarm.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisAlarmCacheService {

    private final RedisAlarmService redisService;
    private final DistributedAlarmService distributedAlarmService;

    public void setAlarm(String key, String value, int timeout) {
        redisService.setValues(key, value);
        redisService.expireValues(key, timeout); // 캐시 만료 시간 설정
    }

    public String getAlarm(String key) {
        return redisService.getValues(key);
    }

    public void deleteAlarm(String key) {
        redisService.deleteValues(key);
    }

    public void updateCache(String key, String value, int timeout) {
        // 캐시 갱신 시 분산 락 획득
        RLock lock = distributedAlarmService.getLock((long) key.hashCode()); // 인자를 Long 형식으로 변환
        try {
            // 락 획득 시도
            if (distributedAlarmService.tryLock(lock, 1000, 3000)) { // 대기 시간 1초, 락 보유 시간 3초
                // 캐시 갱신
                setAlarm(key, value, timeout);
            }
        } finally {
            // 락 해제
            distributedAlarmService.unlock(lock);
        }
    }
}
