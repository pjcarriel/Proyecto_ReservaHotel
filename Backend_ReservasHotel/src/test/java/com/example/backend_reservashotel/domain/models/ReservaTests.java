package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTests {

    private final Huesped huesped = Huesped.nuevo("1712345678", "Carlos", "Mendoza", "carlos@correo.com", "0991234567");
    private final TipoHabitacion tipo = TipoHabitacion.nuevo("Doble", "Habitación doble estándar", 2);

    @Test
    void nuevaValidaFechasYGeneraNumeroConfirmado() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = entrada.plusDays(3);

        Reserva reserva = Reserva.nueva(huesped, entrada, salida);

        assertTrue(reserva.getNumero().startsWith("RES-"));
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        assertTrue(reserva.estaConfirmada());
        assertEquals(3, reserva.calcularNoches());
        assertEquals(new BigDecimal("0.00"), reserva.getTotal());
    }

    @Test
    void nuevaRechazaFechasInvalidas() {
        LocalDate hoy = LocalDate.now();
        assertAll(
                () -> assertThrows(DatoInvalidoException.class,
                        () -> Reserva.nueva(huesped, hoy.minusDays(1), hoy.plusDays(2))),
                () -> assertThrows(DatoInvalidoException.class,
                        () -> Reserva.nueva(huesped, hoy.plusDays(2), hoy.plusDays(2))),
                () -> assertThrows(DatoInvalidoException.class,
                        () -> Reserva.nueva(huesped, hoy.plusDays(3), hoy.plusDays(1)))
        );
    }

    @Test
    void agregarHabitacionCalculaNochesSubtotalIvaYTotal() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = entrada.plusDays(2);
        Reserva reserva = Reserva.nueva(huesped, entrada, salida);

        Habitacion hab = new Habitacion(1L, "101", 1, new BigDecimal("50.00"),
                EstadoHabitacion.DISPONIBLE, tipo, LocalDateTime.now(), LocalDateTime.now());

        reserva.agregarHabitacion(hab);

        assertEquals(1, reserva.getDetalles().size());
        assertEquals(new BigDecimal("100.00"), reserva.getSubtotal());
        assertEquals(new BigDecimal("15.00"), reserva.getIva());
        assertEquals(new BigDecimal("115.00"), reserva.getTotal());
    }

    @Test
    void rechazaHabitacionEnMantenimientoYCancelacionRepetida() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = entrada.plusDays(2);
        Reserva reserva = Reserva.nueva(huesped, entrada, salida);

        Habitacion habMantenimiento = new Habitacion(2L, "102", 1, new BigDecimal("50.00"),
                EstadoHabitacion.MANTENIMIENTO, tipo, LocalDateTime.now(), LocalDateTime.now());

        assertThrows(ReglaNegocioException.class, () -> reserva.agregarHabitacion(habMantenimiento));

        reserva.cancelar();
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertThrows(ReglaNegocioException.class, reserva::cancelar);
    }
}