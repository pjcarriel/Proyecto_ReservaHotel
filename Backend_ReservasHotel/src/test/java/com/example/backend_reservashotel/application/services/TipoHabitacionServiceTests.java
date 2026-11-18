package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.out.TipoHabitacionOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoHabitacionServiceTests {
    @Mock
    private TipoHabitacionOut tipoHabitacionOut;

    @InjectMocks
    private TipoHabitacionService service;

    @Test
    void crearCompruebaElNombreSinEspaciosExternos() {
        TipoHabitacion entrada = new TipoHabitacion(null, "  Doble  ", "Para dos personas", 2, null, null);
        when(tipoHabitacionOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TipoHabitacion resultado = service.crear(entrada);

        assertEquals("Doble", resultado.getNombre());
        verify(tipoHabitacionOut).existePorNombre("Doble", null);
    }

    @Test
    void crearRechazaUnNombreDuplicadoSinGuardar() {
        TipoHabitacion entrada = TipoHabitacion.nuevo("Doble", "Para dos personas", 2);
        when(tipoHabitacionOut.existePorNombre("Doble", null)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.crear(entrada));

        verify(tipoHabitacionOut, never()).guardar(any());
    }

    @Test
    void actualizarPermiteConservarElNombreYLaIdentidad() {
        TipoHabitacion actual = existente();
        LocalDateTime creadoOriginal = actual.getCreado();
        when(tipoHabitacionOut.buscarPorId(1L)).thenReturn(Optional.of(actual));
        when(tipoHabitacionOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TipoHabitacion resultado = service.actualizar(1L,
                TipoHabitacion.nuevo("Doble", "Descripcion actualizada", 3));

        verify(tipoHabitacionOut).existePorNombre("Doble", 1L);
        assertEquals(1L, resultado.getId());
        assertEquals(3, resultado.getCapacidad());
        assertEquals(creadoOriginal, resultado.getCreado());
    }

    @Test
    void actualizarConNombreDuplicadoConservaLosDatosAnteriores() {
        TipoHabitacion actual = existente();
        when(tipoHabitacionOut.buscarPorId(1L)).thenReturn(Optional.of(actual));
        when(tipoHabitacionOut.existePorNombre("Suite", 1L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.actualizar(1L,
                TipoHabitacion.nuevo("Suite", "Descripcion nueva", 4)));

        assertEquals("Doble", actual.getNombre());
        assertEquals("Para dos personas", actual.getDescripcion());
        assertEquals(2, actual.getCapacidad());
        verify(tipoHabitacionOut, never()).guardar(any());
    }

    @Test
    void buscarYEliminarUnIdInexistenteInformanQueNoExiste() {
        when(tipoHabitacionOut.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L));
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(99L));

        verify(tipoHabitacionOut, never()).eliminar(any());
    }

    @Test
    void eliminarCompruebaQueElRegistroExiste() {
        when(tipoHabitacionOut.buscarPorId(1L)).thenReturn(Optional.of(existente()));

        service.eliminar(1L);

        verify(tipoHabitacionOut).eliminar(1L);
    }

    private TipoHabitacion existente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 17, 10, 0);
        return new TipoHabitacion(1L, "Doble", "Para dos personas", 2, fecha, fecha);
    }
}
