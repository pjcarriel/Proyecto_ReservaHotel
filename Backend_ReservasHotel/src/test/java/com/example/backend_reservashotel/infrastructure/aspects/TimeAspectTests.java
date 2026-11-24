package com.example.backend_reservashotel.infrastructure.aspects;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.backend_reservashotel.application.ports.in.HabitacionIn;
import com.example.backend_reservashotel.application.ports.in.TipoHabitacionIn;
import com.example.backend_reservashotel.application.ports.out.HabitacionOut;
import com.example.backend_reservashotel.application.ports.out.TipoHabitacionOut;
import com.example.backend_reservashotel.application.services.HabitacionService;
import com.example.backend_reservashotel.application.services.TipoHabitacionService;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeAspectTests {
    private final TipoHabitacionOut tipoOut = mock(TipoHabitacionOut.class);
    private final HabitacionOut habitacionOut = mock(HabitacionOut.class);
    private final Logger logger = (Logger) LoggerFactory.getLogger(TimeAspect.class);
    private final ListAppender<ILoggingEvent> registros = new ListAppender<>();
    private TipoHabitacionIn service;
    private Level nivelAnterior;

    @BeforeEach
    void preparar() {
        nivelAnterior = logger.getLevel();
        logger.setLevel(Level.INFO);
        registros.start();
        logger.addAppender(registros);
        AspectJProxyFactory factory = new AspectJProxyFactory(new TipoHabitacionService(tipoOut, habitacionOut));
        factory.addAspect(new TimeAspect());
        service = factory.getProxy();
    }

    @AfterEach
    void limpiar() {
        logger.detachAppender(registros);
        logger.setLevel(nivelAnterior);
        registros.stop();
    }

    @Test
    void mideLaOperacionSinCambiarElResultado() {
        List<TipoHabitacion> esperado = List.of(TipoHabitacion.nuevo("Doble", "Para dos personas", 2));
        when(tipoOut.listar()).thenReturn(esperado);

        assertSame(esperado, service.listar());

        assertEquals(1, registros.list.size());
        assertTrue(registros.list.getFirst().getFormattedMessage().contains("Tiempo TipoHabitacionService.listar:"));
    }

    @Test
    void mideTambienCuandoElServicioLanzaUnaExcepcion() {
        IllegalStateException error = new IllegalStateException("Fallo de consulta");
        when(tipoOut.listar()).thenThrow(error);

        assertSame(error, assertThrows(IllegalStateException.class, service::listar));

        assertEquals(1, registros.list.size());
        verify(tipoOut, times(1)).listar();
    }

    @Test
    void noAplicaLaMedicionAUnServicioDeHabitaciones() {
        when(habitacionOut.listar()).thenReturn(List.of());
        AspectJProxyFactory factory = new AspectJProxyFactory(new HabitacionService(habitacionOut, tipoOut));
        factory.addAspect(new TimeAspect());
        HabitacionIn habitaciones = factory.getProxy();

        habitaciones.listar();

        assertTrue(registros.list.isEmpty());
    }
}
