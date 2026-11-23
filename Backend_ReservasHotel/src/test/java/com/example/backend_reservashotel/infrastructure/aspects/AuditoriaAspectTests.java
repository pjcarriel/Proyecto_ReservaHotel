package com.example.backend_reservashotel.infrastructure.aspects;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.backend_reservashotel.application.ports.in.HabitacionIn;
import com.example.backend_reservashotel.application.ports.out.HabitacionOut;
import com.example.backend_reservashotel.application.ports.out.TipoHabitacionOut;
import com.example.backend_reservashotel.application.services.HabitacionService;
import com.example.backend_reservashotel.domain.models.EstadoHabitacion;
import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditoriaAspectTests {
    private final HabitacionOut habitacionOut = mock(HabitacionOut.class);
    private final TipoHabitacionOut tipoOut = mock(TipoHabitacionOut.class);
    private final Logger logger = (Logger) LoggerFactory.getLogger(AuditoriaAspect.class);
    private final ListAppender<ILoggingEvent> registros = new ListAppender<>();
    private final TipoHabitacion tipo = new TipoHabitacion(1L, "Doble", "Para dos personas", 2, null, null);
    private HabitacionIn service;
    private Level nivelAnterior;

    @BeforeEach
    void preparar() {
        nivelAnterior = logger.getLevel();
        logger.setLevel(Level.INFO);
        registros.start();
        logger.addAppender(registros);
        AspectJProxyFactory factory = new AspectJProxyFactory(new HabitacionService(habitacionOut, tipoOut));
        factory.addAspect(new AuditoriaAspect());
        service = factory.getProxy();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
        logger.detachAppender(registros);
        logger.setLevel(nivelAnterior);
        registros.stop();
    }

    @Test
    void crearRegistraUsuarioOperacionEIdSinCambiarElResultado() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("stalin", "", List.of()));
        Habitacion guardada = habitacion();
        when(tipoOut.buscarPorId(1L)).thenReturn(Optional.of(tipo));
        when(habitacionOut.guardar(any())).thenReturn(guardada);

        assertSame(guardada, service.crear(habitacion(), 1L));

        assertEquals(1, registros.list.size());
        assertTrue(mensaje().contains("usuario=stalin operacion=crear habitacionId=7 estado=EXITO"));
        verify(habitacionOut, times(1)).guardar(any());
    }

    @Test
    void actualizarSeAuditaComoModificacion() {
        Habitacion actual = habitacion();
        when(habitacionOut.buscarPorId(7L)).thenReturn(Optional.of(actual));
        when(tipoOut.buscarPorId(1L)).thenReturn(Optional.of(tipo));
        when(habitacionOut.guardar(any())).thenReturn(actual);

        service.actualizar(7L, habitacion(), 1L);

        assertEquals(1, registros.list.size());
        assertTrue(mensaje().contains("usuario=anonimo operacion=actualizar habitacionId=7 estado=EXITO"));
    }

    @Test
    void eliminarRegistraElIdAunqueElMetodoDevuelveVoid() {
        when(habitacionOut.buscarPorId(7L)).thenReturn(Optional.of(habitacion()));

        service.eliminar(7L);

        verify(habitacionOut).eliminar(7L);
        assertEquals(1, registros.list.size());
        assertTrue(mensaje().contains("operacion=eliminar habitacionId=7 estado=EXITO"));
    }

    @Test
    void lasConsultasNoSeAuditanComoModificaciones() {
        when(habitacionOut.listar()).thenReturn(List.of(habitacion()));
        when(habitacionOut.buscarPorId(7L)).thenReturn(Optional.of(habitacion()));

        service.listar();
        service.buscarPorId(7L);

        assertTrue(registros.list.isEmpty());
    }

    @Test
    void unFalloSeRegistraComoErrorYConservaLaExcepcionOriginal() {
        IllegalStateException error = new IllegalStateException("Fallo de persistencia");
        when(tipoOut.buscarPorId(1L)).thenReturn(Optional.of(tipo));
        when(habitacionOut.guardar(any())).thenThrow(error);

        assertSame(error, assertThrows(IllegalStateException.class, () -> service.crear(habitacion(), 1L)));

        assertEquals(1, registros.list.size());
        assertEquals(Level.WARN, registros.list.getFirst().getLevel());
        assertTrue(mensaje().contains("estado=ERROR tipo=IllegalStateException"));
        assertFalse(mensaje().contains("estado=EXITO"));
    }

    private String mensaje() {
        return registros.list.getFirst().getFormattedMessage();
    }

    private Habitacion habitacion() {
        return new Habitacion(7L, "101", 1, new BigDecimal("50.00"),
                EstadoHabitacion.DISPONIBLE, tipo, null, null);
    }
}
