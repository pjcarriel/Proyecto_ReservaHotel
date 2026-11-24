package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.out.PasswordOut;
import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {

    @Mock
    private UsuarioOut usuarioOut;

    @Mock
    private PasswordOut passwordOut;

    @InjectMocks
    private UsuarioService service;

    @Test
    void crearEncriptaElPasswordYIniciaActivo() {
        when(passwordOut.encriptar("clave12345")).thenReturn("$2a$10$hash");
        when(usuarioOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = service.crear(Usuario.nuevo("recepcion1", "clave12345", Rol.RECEPCIONISTA));

        assertEquals("$2a$10$hash", resultado.getPassword());
        assertTrue(resultado.isActivo());
    }

    @Test
    void crearRechazaUsernameDuplicado() {
        when(usuarioOut.existePorUsername("recepcion1", null)).thenReturn(true);

        assertThrows(ReglaNegocioException.class,
                () -> service.crear(Usuario.nuevo("recepcion1", "clave12345", Rol.RECEPCIONISTA)));

        verify(usuarioOut, never()).guardar(any());
    }

    @Test
    void actualizarEncriptaElNuevoPassword() {
        when(usuarioOut.buscarPorId(2L)).thenReturn(Optional.of(recepcionista()));
        when(passwordOut.encriptar("nuevaClave1")).thenReturn("$2a$10$otroHash");
        when(usuarioOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = service.actualizar(2L, Usuario.nuevo("recepcion2", "nuevaClave1", Rol.ADMIN));

        assertEquals("recepcion2", resultado.getUsername());
        assertEquals("$2a$10$otroHash", resultado.getPassword());
        assertEquals(Rol.ADMIN, resultado.getRol());
        verify(usuarioOut).existePorUsername("recepcion2", 2L);
    }

    @Test
    void cambiarEstadoDesactivaElUsuario() {
        when(usuarioOut.buscarPorId(2L)).thenReturn(Optional.of(recepcionista()));
        when(usuarioOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertFalse(service.cambiarEstado(2L, false).isActivo());
    }

    @Test
    void eliminarUnIdInexistenteLanzaNoEncontrado() {
        when(usuarioOut.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(99L));

        verify(usuarioOut, never()).eliminar(anyLong());
    }

    private static Usuario recepcionista() {
        return new Usuario(2L, "recepcion1", "$2a$10$hashActual", Rol.RECEPCIONISTA, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
