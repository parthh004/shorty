package com.tss.shorty.interceptor;

import com.tss.shorty.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
    private final RateLimitingService rateLimitingService;

    public RateLimitingInterceptor(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ip = request.getHeader("X-Forward-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        Bucket bucket = rateLimitingService.resolveBucket(ip);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_00_000_00;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                    "You have exhausted your api request quota, try again later");
            return false;
        }
    }
}
