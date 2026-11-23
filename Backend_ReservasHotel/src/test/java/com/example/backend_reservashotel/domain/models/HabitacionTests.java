package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HabitacionTests {
    private final TipoHabitacion tipo = TipoHabitacion.nuevo("Doble", "Para dos personas", 2);

    @ParameterizedTest
    @ValueSource(ints = {-1, Integer.MIN_VALUE})
    void crearYActualizarRechazanPisosNegativosSinModificarLaHabitacion(int piso) {
        assertThrows(DatoInvalidoException.class,
                () -> Habitacion.nuevo("101", piso, new BigDecimal("45.50"), EstadoHabitacion.DISPONIBLE, tipo));

        Habitacion actual = Habitacion.nuevo("101", 1, new BigDecimal("45.50"),
                EstadoHabitacion.DISPONIBLE, tipo);

        assertThrows(DatoInvalidoException.class,
                () -> actual.actualizar("202", piso, new BigDecimal("60.25"), EstadoHabitacion.MANTENIMIENTO, tipo));

        assertAll(
                () -> assertEquals("101", actual.getNumero()),
                () -> assertEquals(1, actual.getPiso()),
                () -> assertEquals(new BigDecimal("45.50"), actual.getPrecioNoche()),
                () -> assertTrue(actual.estaDisponible())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 15})
    void crearYActualizarPermitenPlantaBajaYPisosPositivos(int piso) {
        Habitacion actual = Habitacion.nuevo("101", piso, new BigDecimal("45.50"),
                EstadoHabitacion.DISPONIBLE, tipo);
        assertEquals(piso, actual.getPiso());

        actual.actualizar("202", piso, new BigDecimal("60.25"), EstadoHabitacion.MANTENIMIENTO, tipo);
        assertEquals(piso, actual.getPiso());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-0.01"})
    void nuevoRechazaUnPrecioAusenteOCeroONegativo(String valor) {
        BigDecimal precio = valor == null ? null : new BigDecimal(valor);

        assertThrows(DatoInvalidoException.class,
                () -> Habitacion.nuevo("101", 1, precio, EstadoHabitacion.DISPONIBLE, tipo));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void nuevoRechazaUnNumeroVacio(String numero) {
        assertThrows(DatoInvalidoException.class,
                () -> Habitacion.nuevo(numero, 1, new BigDecimal("45.50"), EstadoHabitacion.DISPONIBLE, tipo));
    }

    @Test
    void nuevoRequiereEstadoYTipo() {
        assertAll(
                () -> assertThrows(DatoInvalidoException.class,
                        () -> Habitacion.nuevo("101", 1, new BigDecimal("45.50"), null, tipo)),
                () -> assertThrows(DatoInvalidoException.class,
                        () -> Habitacion.nuevo("101", 1, new BigDecimal("45.50"), EstadoHabitacion.DISPONIBLE, null))
        );
    }

    @Test
    void actualizarConPrecioInvalidoConservaTodosLosDatos() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 17, 10, 0);
        Habitacion actual = new Habitacion(1L, "101", 1, new BigDecimal("45.50"),
                EstadoHabitacion.DISPONIBLE, tipo, fecha, fecha);

        assertThrows(DatoInvalidoException.class,
                () -> actual.actualizar("202", 2, BigDecimal.ZERO, EstadoHabitacion.MANTENIMIENTO, tipo));

        assertAll(
                () -> assertEquals("101", actual.getNumero()),
                () -> assertEquals(1, actual.getPiso()),
                () -> assertEquals(new BigDecimal("45.50"), actual.getPrecioNoche()),
                () -> assertTrue(actual.estaDisponible()),
                () -> assertEquals(1L, actual.getId()),
                () -> assertEquals(fecha, actual.getCreado()),
                () -> assertEquals(fecha, actual.getModificado())
        );
    }

    @Test
    void actualizarCambiaPrecioYEstadoConservandoLaPrecisionDecimal() {
        Habitacion actual = Habitacion.nuevo(" 101 ", 1, new BigDecimal("45.50"),
                EstadoHabitacion.DISPONIBLE, tipo);

        assertEquals("101", actual.getNumero());
        actual.actualizar(" 202 ", 2, new BigDecimal("60.25"), EstadoHabitacion.MANTENIMIENTO, tipo);

        assertEquals("202", actual.getNumero());
        assertEquals(new BigDecimal("60.25"), actual.getPrecioNoche());
        assertFalse(actual.estaDisponible());
    }
}
