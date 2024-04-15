package com.sparta.finalticket.global.config;

import com.sparta.finalticket.global.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class LoginConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {//적용 위치
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
            .excludePathPatterns("/v1/users/login")
            .excludePathPatterns("/v1/users/signup")
            .excludePathPatterns("/v1/users/login-page")
            .excludePathPatterns("/css/**")
            .excludePathPatterns("/js/**");
    }
}
