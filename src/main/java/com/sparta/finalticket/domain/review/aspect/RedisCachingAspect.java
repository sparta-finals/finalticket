package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCachingAspect {

    private final RedisCacheService redisCacheService;

    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.*Service.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object cacheServiceData(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String cacheKey = generateCacheKey(methodName, args);

        // Check if data is cached
        Object cachedData = redisCacheService.getCachedData(cacheKey);
        if (cachedData != null) {
            return cachedData;
        }

        // Proceed with method execution
        result = joinPoint.proceed();

        // Cache the result
        redisCacheService.cacheData(cacheKey, result);

        return result;
    }

    private String generateCacheKey(String methodName, Object[] args) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(methodName);
        for (Object arg : args) {
            keyBuilder.append(":").append(arg.toString());
        }
        return keyBuilder.toString();
    }
}
