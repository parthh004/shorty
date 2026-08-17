package com.tss.shorty.config;

import com.tss.shorty.interceptor.RateLimitingInterceptor;
import com.tss.shorty.service.RateLimitingService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RateLimitingInterceptor rateLimitingInterceptor;

    public WebConfig(RateLimitingInterceptor rateLimitingInterceptor) {
        this.rateLimitingInterceptor = rateLimitingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/*", "/api/v1/auth/**")
                .excludePathPatterns(
                        "/api/v1/admin/**",
                        "/api/v1/pricing",
                        "/api/v1/transaction/**",
                        "/api/v1/urls/**",
                        "/api/v1/users/**"
                );
    }
}
