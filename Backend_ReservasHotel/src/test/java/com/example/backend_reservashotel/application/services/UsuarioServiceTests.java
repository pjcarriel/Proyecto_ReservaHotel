package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.out.PasswordOut;
import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {
    private static final String HASH_ACTUAL = "$2a$10$hashActual";

    @Mock
    private UsuarioOut usuarioOut;

    @Mock
    private PasswordOut passwordOut;

    @InjectMocks
    private UsuarioService service;

    @Test
    void crearEncriptaElPasswordYIniciaActivo() {
        when(passwordOut.encriptar("clave12345")).thenReturn("$2a$10$hashNuevo");
        when(usuarioOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = service.crear(Usuario.nuevo("  recepcion1 ", " Ana Pérez ", "clave12345", Rol.RECEPCIONISTA));

        assertEquals("$2a$10$hashNuevo", resultado.getPassword());
        assertEquals("recepcion1", resultado.getUsername());
        assertEquals("Ana Pérez", resultado.getNombre());
        assertTrue(resultado.isActivo());
        verify(usuarioOut).existePorUsername("recepcion1", null);
    }

    @Test
    void crearExigePassword() {
        Usuario entrada = Usuario.nuevo("recepcion1", "Ana Pérez", "  ", Rol.RECEPCIONISTA);

        assertThrows(DatoInvalidoException.class, () -> service.crear(entrada));

        verify(usuarioOut, never()).guardar(any());
    }

    @Test
    void crearRechazaPasswordCorto() {
        Usuario entrada = Usuario.nuevo("recepcion1", "Ana Pérez", "corta", Rol.RECEPCIONISTA);

        assertThrows(DatoInvalidoException.class, () -> service.crear(entrada));

        verify(passwordOut, never()).encriptar(anyString());
    }

    @Test
    void crearRechazaUsernameDuplicadoSinGuardar() {
        when(usuarioOut.existePorUsername("recepcion1", null)).thenReturn(true);

        assertThrows(ReglaNegocioException.class,
                () -> service.crear(Usuario.nuevo("recepcion1", "Ana Pérez", "clave12345", Rol.RECEPCIONISTA)));

        verify(passwordOut, never()).encriptar(anyString());
        verify(usuarioOut, never()).guardar(any());
    }

    @Test
    void actualizarSinPasswordConservaElHashActual() {
        when(usuarioOut.buscarPorId(2L)).thenReturn(Optional.of(recepcionista()));
        when(usuarioOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = service.actualizar(2L, Usuario.nuevo("recepcion2", "Ana María", null, Rol.RECEPCIONISTA));

        assertEquals(HASH_ACTUAL, resultado.getPassword());
        assertEquals("recepcion2", resultado.getUsername());
        verify(passwordOut, never()).encriptar(anyString());
        verify(usuarioOut).existePorUsername("recepcion2", 2L);
    }

    @Test
    void actualizarConPasswordLoVuelveAEncriptar() {
        when(usuarioOut.buscarPorId(2L)).thenReturn(Optional.of(recepcionista()));
        when(passwordOut.encriptar("nuevaClave1")).thenReturn("$2a$10$otroHash");
        when(usuarioOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = service.actualizar(2L,
                Usuario.nuevo("recepcion1", "Ana Pérez", "nuevaClave1", Rol.RECEPCIONISTA));

        assertEquals("$2a$10$otroHash", resultado.getPassword());
    }

    @Test
    void actualizarNoQuitaElRolAlUltimoAdmin() {
        when(usuarioOut.buscarPorId(1L)).thenReturn(Optional.of(admin()));
        when(usuarioOut.contarAdminsActivos()).thenReturn(1L);

        assertThrows(ReglaNegocioException.class,
                () -> service.actualizar(1L, Usuario.nuevo("admin", "Administrador", null, Rol.RECEPCIONISTA)));

        verify(usuarioOut, never()).guardar(any());
    }

    @Test
    void cambiarEstadoDesactivaUnRecepcionista() {
        when(usuarioOut.buscarPorId(2L)).thenReturn(Optional.of(recepcionista()));
        when(usuarioOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = service.cambiarEstado(2L, false);

        assertFalse(resultado.isActivo());
        verify(usuarioOut, never()).contarAdminsActivos();
    }

    @Test
    void cambiarEstadoNoDesactivaAlUltimoAdmin() {
        when(usuarioOut.buscarPorId(1L)).thenReturn(Optional.of(admin()));
        when(usuarioOut.contarAdminsActivos()).thenReturn(1L);

        assertThrows(ReglaNegocioException.class, () -> service.cambiarEstado(1L, false));

        verify(usuarioOut, never()).guardar(any());
    }

    @Test
    void cambiarEstadoDesactivaUnAdminSiHayOtro() {
        when(usuarioOut.buscarPorId(1L)).thenReturn(Optional.of(admin()));
        when(usuarioOut.contarAdminsActivos()).thenReturn(2L);
        when(usuarioOut.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertFalse(service.cambiarEstado(1L, false).isActivo());
    }

    @Test
    void eliminarNoBorraAlUltimoAdmin() {
        when(usuarioOut.buscarPorId(1L)).thenReturn(Optional.of(admin()));
        when(usuarioOut.contarAdminsActivos()).thenReturn(1L);

        assertThrows(ReglaNegocioException.class, () -> service.eliminar(1L));

        verify(usuarioOut, never()).eliminar(anyLong());
    }

    @Test
    void buscarUnIdInexistenteLanzaNoEncontrado() {
        when(usuarioOut.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    private static Usuario admin() {
        return new Usuario(1L, "admin", "Administrador", HASH_ACTUAL, Rol.ADMIN, true,
                LocalDateTime.now(), LocalDateTime.now());
    }

    private static Usuario recepcionista() {
        return new Usuario(2L, "recepcion1", "Ana Pérez", HASH_ACTUAL, Rol.RECEPCIONISTA, true,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
