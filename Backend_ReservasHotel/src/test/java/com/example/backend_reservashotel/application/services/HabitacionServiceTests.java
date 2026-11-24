package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.out.HabitacionOut;
import com.example.backend_reservashotel.application.ports.out.TipoHabitacionOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.EstadoHabitacion;
import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitacionServiceTests {
    @Mock
    private HabitacionOut habitacionOut;
    @Mock
    private TipoHabitacionOut tipoHabitacionOut;
    @InjectMocks
    private HabitacionService service;

    @Test
    void crearResuelveElTipoYCompruebaElNumeroNormalizado() {
        when(tipoHabitacionOut.buscarPorId(1L)).thenReturn(Optional.of(tipo(1L)));
        when(habitacionOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Habitacion creada = service.crear(datos("  A-101  "), 1L);

        assertEquals("A-101", creada.getNumero());
        assertEquals(1L, creada.getTipoHabitacion().getId());
        assertNull(creada.getId());
        verify(habitacionOut).existePorNumero("A-101", null);
    }

    @Test
    void crearRechazaUnTipoInexistenteSinGuardar() {
        when(tipoHabitacionOut.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.crear(datos("101"), 99L));

        verify(habitacionOut, never()).guardar(any());
    }

    @Test
    void crearRechazaUnNumeroDuplicadoSinGuardar() {
        when(tipoHabitacionOut.buscarPorId(1L)).thenReturn(Optional.of(tipo(1L)));
        when(habitacionOut.existePorNumero("101", null)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.crear(datos("101"), 1L));

        verify(habitacionOut, never()).guardar(any());
    }

    @Test
    void actualizarExcluyeElPropioIdYCambiaElTipoDesdeLaConsulta() {
        Habitacion actual = existente();
        LocalDateTime creado = actual.getCreado();
        when(habitacionOut.buscarPorId(7L)).thenReturn(Optional.of(actual));
        when(tipoHabitacionOut.buscarPorId(2L)).thenReturn(Optional.of(tipo(2L)));
        when(habitacionOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Habitacion actualizada = service.actualizar(7L, datos(" 102 "), 2L);

        verify(habitacionOut).existePorNumero("102", 7L);
        assertEquals(7L, actualizada.getId());
        assertEquals(2L, actualizada.getTipoHabitacion().getId());
        assertEquals("102", actualizada.getNumero());
        assertEquals(creado, actualizada.getCreado());
    }

    @Test
    void actualizarConNumeroDuplicadoConservaLosDatosAnteriores() {
        Habitacion actual = existente();
        when(habitacionOut.buscarPorId(7L)).thenReturn(Optional.of(actual));
        when(tipoHabitacionOut.buscarPorId(2L)).thenReturn(Optional.of(tipo(2L)));
        when(habitacionOut.existePorNumero("102", 7L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.actualizar(7L, datos("102"), 2L));

        assertEquals("101", actual.getNumero());
        assertEquals(1L, actual.getTipoHabitacion().getId());
        verify(habitacionOut, never()).guardar(any());
    }

    @Test
    void buscarYEliminarUnaHabitacionInexistenteDevuelvenError() {
        when(habitacionOut.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L));
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(99L));

        verify(habitacionOut, never()).eliminar(any());
    }

    private Habitacion datos(String numero) {
        return new Habitacion(null, numero, 0, new BigDecimal("45.50"),
                EstadoHabitacion.DISPONIBLE, null, null, null);
    }

    private Habitacion existente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 22, 10, 0);
        return new Habitacion(7L, "101", 1, new BigDecimal("50.00"),
                EstadoHabitacion.DISPONIBLE, tipo(1L), fecha, fecha);
    }

    private TipoHabitacion tipo(Long id) {
        return new TipoHabitacion(id, "Tipo " + id, "Descripcion del tipo de prueba", 2, null, null);
    }
}
