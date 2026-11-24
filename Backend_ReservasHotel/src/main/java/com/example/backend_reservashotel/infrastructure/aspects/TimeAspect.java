package com.example.backend_reservashotel.infrastructure.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(0)
public class TimeAspect {
    private static final Logger log = LoggerFactory.getLogger(TimeAspect.class);

    @Around("execution(public * com.example.backend_reservashotel.application.services.TipoHabitacionService.*(..))")
    public Object medir(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long milisegundos = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicio);
            log.info("Tiempo TipoHabitacionService.{}: {} ms", joinPoint.getSignature().getName(), milisegundos);
        }
    }
}
