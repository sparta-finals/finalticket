package com.sparta.finalticket.global.config.aspect;

import com.sparta.finalticket.domain.review.aspect.ReviewCacheAspect;
import com.sparta.finalticket.domain.review.service.RedisCacheService;
import com.sparta.finalticket.domain.review.service.RedisReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class AspectConfig {

    private final RedisCacheService redisCacheService;
    private final RedisReviewService redisReviewService;

    @Bean
    public ReviewCacheAspect reviewCacheAspect() {
        return new ReviewCacheAspect(redisCacheService, redisReviewService); // 두 개의 서비스 인자로 전달
    }
}
