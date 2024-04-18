package com.sparta.finalticket.global.config;

import com.sparta.finalticket.domain.review.aspect.ReviewCacheAspect;
import com.sparta.finalticket.domain.review.service.ReviewCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class AspectConfig {

    private final ReviewCacheService reviewCacheService;

    @Bean
    public ReviewCacheAspect reviewCacheAspect() {
        return new ReviewCacheAspect(reviewCacheService);
    }
}
