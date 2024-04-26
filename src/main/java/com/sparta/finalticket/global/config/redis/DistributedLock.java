package com.sparta.finalticket.global.config.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD) //어노테이션 적용할곳
@Retention(RetentionPolicy.RUNTIME) //적용시점
public @interface DistributedLock {
    String[] key();
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long waitTime() default 5L;

    long leaseTime() default 3L;
}
