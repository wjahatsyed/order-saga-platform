package com.wajahat.ordersaga.order.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class RequestResponseLoggingAspectTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void proceedsAndAddsTraceId() throws Throwable {
        RequestResponseLoggingAspect aspect = new RequestResponseLoggingAspect(new SensitiveDataSanitizer());
        ProceedingJoinPoint joinPoint = joinPoint("ok");

        Object response = aspect.logControllerCall(joinPoint);

        assertEquals("ok", response);
        assertNotNull(MDC.get("traceId"));
    }

    @Test
    void rethrowsControllerException() throws Throwable {
        RequestResponseLoggingAspect aspect = new RequestResponseLoggingAspect(new SensitiveDataSanitizer());
        ProceedingJoinPoint joinPoint = joinPoint(new IllegalStateException("boom"));

        assertThrows(IllegalStateException.class, () -> aspect.logControllerCall(joinPoint));
        assertNotNull(MDC.get("traceId"));
    }

    private ProceedingJoinPoint joinPoint(Object resultOrException) throws Throwable {
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getDeclaringType()).thenReturn(DummyController.class);
        when(signature.getName()).thenReturn("handle");

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{MapRequest.of("token", "secret-token")});
        if (resultOrException instanceof Throwable throwable) {
            when(joinPoint.proceed()).thenThrow(throwable);
        } else {
            when(joinPoint.proceed()).thenReturn(resultOrException);
        }
        return joinPoint;
    }

    private static class DummyController {
    }

    private record MapRequest(String token, String value) {
        static MapRequest of(String token, String value) {
            return new MapRequest(token, value);
        }
    }
}
