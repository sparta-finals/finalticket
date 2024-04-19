package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.converter.ReviewConverter;
import com.sparta.finalticket.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheReviewData(String key, Review reviewData) {
        // Review 객체를 Redis Hash 자료구조에 저장
        redisTemplate.opsForHash().putAll(key, ReviewConverter.convertReviewToMap(reviewData));
    }

    public Set<String> getAllKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void setValues(String key, String data) {
        redisTemplate.opsForValue().set(key, data);
    }

    public void setValues(String key, String data, Duration duration) {
        redisTemplate.opsForValue().set(key, data, duration);
    }

    public String getValues(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void expireValues(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    public void setHashOps(String key, Map<String, String> data) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.putAll(key, data);
    }

    public String getHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) values.get(key, hashKey) : null;
    }

    public void deleteHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(key, hashKey);
    }

    public boolean checkExistsValue(String value) {
        return value != null;
    }
}
