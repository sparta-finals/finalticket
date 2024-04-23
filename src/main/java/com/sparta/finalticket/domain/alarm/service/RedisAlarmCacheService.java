package com.sparta.finalticket.domain.alarm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisAlarmCacheService {

    private final RedisAlarmService redisService;

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
}

