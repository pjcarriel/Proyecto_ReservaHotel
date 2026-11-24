package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.out.HabitacionOut;
import com.example.backend_reservashotel.application.ports.out.HuespedOut;
import com.example.backend_reservashotel.application.ports.out.ReservaOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.EstadoHabitacion;
import com.example.backend_reservashotel.domain.models.EstadoReserva;
import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.domain.models.Huesped;
import com.example.backend_reservashotel.domain.models.Reserva;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTests {

    @Mock
    private ReservaOut reservaOut;

    @Mock
    private HuespedOut huespedOut;

    @Mock
    private HabitacionOut habitacionOut;

    @InjectMocks
    private ReservaService service;

    @Test
    void crearCalculaTotalesYGuardaCuandoHabitacionEstaLibreEnFechas() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = LocalDate.now().plusDays(3);
        when(huespedOut.buscarPorId(1L)).thenReturn(Optional.of(huesped()));
        when(habitacionOut.buscarPorId(10L)).thenReturn(Optional.of(habitacion(10L, "101")));
        when(reservaOut.existeChoqueFechas(10L, entrada, salida)).thenReturn(false);
        when(reservaOut.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva resultado = service.crear(1L, entrada, salida, List.of(10L));

        assertEquals(EstadoReserva.CONFIRMADA, resultado.getEstado());
        assertEquals(new BigDecimal("100.00"), resultado.getSubtotal());
        assertEquals(new BigDecimal("15.00"), resultado.getIva());
        assertEquals(new BigDecimal("115.00"), resultado.getTotal());
        verify(reservaOut).guardar(any());
    }

    @Test
    void crearRechazaHabitacionConReservaConfirmadaEnMismasFechas() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = LocalDate.now().plusDays(3);
        when(huespedOut.buscarPorId(1L)).thenReturn(Optional.of(huesped()));
        when(habitacionOut.buscarPorId(10L)).thenReturn(Optional.of(habitacion(10L, "101")));
        when(reservaOut.existeChoqueFechas(10L, entrada, salida)).thenReturn(true);

        assertThrows(ReglaNegocioException.class,
                () -> service.crear(1L, entrada, salida, List.of(10L)));

        verify(reservaOut, never()).guardar(any());
    }

    @Test
    void crearFallaSiHuespedNoExiste() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = LocalDate.now().plusDays(2);
        when(huespedOut.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.crear(99L, entrada, salida, List.of(10L)));

        verify(reservaOut, never()).guardar(any());
    }

    private Huesped huesped() {
        LocalDateTime ahora = LocalDateTime.now();
        return new Huesped(1L, "1710034065", "Ana", "Pérez", "ana@mail.com", "0999999999", ahora, ahora);
    }

    private Habitacion habitacion(Long id, String numero) {
        LocalDateTime ahora = LocalDateTime.now();
        TipoHabitacion tipo = new TipoHabitacion(1L, "Doble", "Para 2", 2, ahora, ahora);
        return new Habitacion(id, numero, 1, new BigDecimal("50.00"), EstadoHabitacion.DISPONIBLE, tipo, ahora, ahora);
    }
}