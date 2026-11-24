package com.example.backend_reservashotel.infrastructure.aspects;

import com.example.backend_reservashotel.domain.models.Habitacion;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
// Envuelve la transaccion: EXITO se registra despues de que esta termine correctamente.
@Order(0)
public class AuditoriaAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditoriaAspect.class);

    @Around("execution(* com.example.backend_reservashotel.application.services.HabitacionService.crear(..)) || "
            + "execution(* com.example.backend_reservashotel.application.services.HabitacionService.actualizar(..)) || "
            + "execution(* com.example.backend_reservashotel.application.services.HabitacionService.eliminar(..))")
    public Object auditar(ProceedingJoinPoint joinPoint) throws Throwable {
        String operacion = joinPoint.getSignature().getName();
        String usuario = usuarioActual();
        Long id = joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof Long valor ? valor : null;
        try {
            Object resultado = joinPoint.proceed();
            if (resultado instanceof Habitacion habitacion) {
                id = habitacion.getId();
            }
            log.info("Auditoria habitacion: usuario={} operacion={} habitacionId={} estado=EXITO", usuario, operacion, id);
            return resultado;
        } catch (Throwable error) {
            log.warn("Auditoria habitacion: usuario={} operacion={} habitacionId={} estado=ERROR tipo={}",
                    usuario, operacion, id, error.getClass().getSimpleName());
            throw error;
        }
    }

    private String usuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "anonimo";
        }
        return authentication.getName().replace('\n', '_').replace('\r', '_');
    }
}
