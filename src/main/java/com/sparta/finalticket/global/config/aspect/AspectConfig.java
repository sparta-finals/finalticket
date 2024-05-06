package com.sparta.finalticket.global.config.aspect;

import com.sparta.finalticket.domain.review.aspect.RedisCacheAspect;
import com.sparta.finalticket.domain.review.service.RedisCacheService;
import com.sparta.finalticket.domain.review.service.RedisReviewService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class AspectConfig {

    private final RedisCacheService redisCacheService;


    @Bean
    public RedisCacheAspect reviewCacheAspect() {
        return new RedisCacheAspect(redisCacheService);
    }
}
