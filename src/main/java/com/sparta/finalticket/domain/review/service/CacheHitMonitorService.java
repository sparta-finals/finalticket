package com.sparta.finalticket.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheHitMonitorService {

    private int cacheHits = 0;
    private int cacheMisses = 0;

    private final RedisService redisService;

    public void monitorCacheHit(String cacheKey) {
        // 캐시 적중 여부 확인
        if (redisService.getValues(cacheKey) != null) {
            // 캐시 적중
            cacheHits++;
        } else {
            // 캐시 미적중
            cacheMisses++;
        }
    }

    public double calculateCacheHitRatio() {
        // 적중률 계산
        if (cacheHits + cacheMisses == 0) {
            return 0.0; // 캐시 적중 또는 미적중이 없는 경우, 적중률은 0
        }
        return (double) cacheHits / (cacheHits + cacheMisses);
    }

    public void resetCacheStatistics() {
        // 캐시 통계 초기화
        cacheHits = 0;
        cacheMisses = 0;
    }
}
