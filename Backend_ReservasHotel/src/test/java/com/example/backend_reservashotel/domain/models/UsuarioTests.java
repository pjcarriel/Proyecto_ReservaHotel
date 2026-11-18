package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTests {

    @Test
    void nuevoRecortaEspaciosYIniciaActivo() {
        Usuario usuario = Usuario.nuevo("  admin ", "  Administrador ", "hash", Rol.ADMIN);

        assertEquals("admin", usuario.getUsername());
        assertEquals("Administrador", usuario.getNombre());
        assertTrue(usuario.isActivo());
        assertTrue(usuario.esAdminActivo());
    }

    @Test
    void nuevoRechazaDatosVacios() {
        assertThrows(DatoInvalidoException.class, () -> Usuario.nuevo(" ", "Nombre", "hash", Rol.ADMIN));
        assertThrows(DatoInvalidoException.class, () -> Usuario.nuevo("admin", null, "hash", Rol.ADMIN));
        assertThrows(DatoInvalidoException.class, () -> Usuario.nuevo("admin", "Nombre", "hash", null));
    }

    @Test
    void nuevoRechazaUsernameConEspacios() {
        assertThrows(DatoInvalidoException.class, () -> Usuario.nuevo("mi admin", "Nombre", "hash", Rol.ADMIN));
    }

    @Test
    void unAdminDesactivadoNoCuentaComoAdminActivo() {
        Usuario usuario = Usuario.nuevo("admin", "Administrador", "hash", Rol.ADMIN);

        usuario.cambiarEstado(false);

        assertFalse(usuario.esAdminActivo());
    }

    @Test
    void cambiarPasswordNoAceptaVacio() {
        Usuario usuario = Usuario.nuevo("admin", "Administrador", "hash", Rol.ADMIN);

        assertThrows(DatoInvalidoException.class, () -> usuario.cambiarPassword(""));
    }
}
