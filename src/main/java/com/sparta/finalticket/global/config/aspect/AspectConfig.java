package com.sparta.finalticket.global.config.aspect;

import com.sparta.finalticket.domain.review.aspect.RedisCacheAspect;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class AspectConfig {

    private final ReviewCacheService reviewCacheService;

    @Bean
    public RedisCacheAspect reviewCacheAspect() {
        return new RedisCacheAspect(reviewCacheService);
    }
}
