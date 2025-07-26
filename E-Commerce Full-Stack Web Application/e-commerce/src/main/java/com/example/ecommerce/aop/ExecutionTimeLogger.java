package com.example.ecommerce.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeLogger {

    private static final long SLOW_ENDPOINT_THRESHOLD_MS = 1000;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        String methodName = joinPoint.getSignature().getName();

        if (duration > SLOW_ENDPOINT_THRESHOLD_MS) {
            log.warn("⚠️ SLOW METHOD [{}] took {} ms", methodName, duration);
        } else {
            log.info("✅ Method [{}] executed in {} ms", methodName, duration);
        }

        return result;
    }
}
