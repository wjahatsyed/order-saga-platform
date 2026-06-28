package com.wajahat.ordersaga.order.logging;

import java.util.Arrays;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestResponseLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingAspect.class);
    private static final String TRACE_ID = "traceId";

    private final SensitiveDataSanitizer sanitizer;

    public RequestResponseLoggingAspect() {
        this(new SensitiveDataSanitizer());
    }

    RequestResponseLoggingAspect(SensitiveDataSanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = ensureTraceId();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String target = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        Object sanitizedArgs = sanitizer.sanitize(Arrays.asList(joinPoint.getArgs()));
        long startedAt = System.nanoTime();

        try {
            Object response = joinPoint.proceed();
            long elapsedMs = elapsedMs(startedAt);
            log.info(
                    "traceId={} method={} args={} response={} executionTimeMs={}",
                    traceId,
                    target,
                    sanitizedArgs,
                    sanitizer.sanitize(response),
                    elapsedMs
            );
            return response;
        } catch (Throwable exception) {
            long elapsedMs = elapsedMs(startedAt);
            log.warn(
                    "traceId={} method={} args={} exceptionType={} exceptionMessage={} executionTimeMs={}",
                    traceId,
                    target,
                    sanitizedArgs,
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    elapsedMs
            );
            throw exception;
        }
    }

    private String ensureTraceId() {
        String traceId = MDC.get(TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID, traceId);
        }
        return traceId;
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
