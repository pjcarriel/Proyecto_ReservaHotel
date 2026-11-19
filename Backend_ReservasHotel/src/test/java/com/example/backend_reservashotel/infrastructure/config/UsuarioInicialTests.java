package com.example.backend_reservashotel.infrastructure.config;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioInicialTests {
    @Mock
    private UsuarioOut usuarioOut;

    @Mock
    private UsuarioIn usuarioIn;

    @Test
    void creaElAdminSiNoHayUsuarios() throws Exception {
        when(usuarioOut.existeAlguno()).thenReturn(false);

        new UsuarioInicial().crearAdmin(usuarioOut, usuarioIn, "admin", "Admin2026*").run();

        ArgumentCaptor<Usuario> creado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioIn).crear(creado.capture());
        assertEquals("admin", creado.getValue().getUsername());
        assertEquals(Rol.ADMIN, creado.getValue().getRol());
        // va en texto plano: UsuarioService es quien lo encripta
        assertEquals("Admin2026*", creado.getValue().getPassword());
    }

    @Test
    void noHaceNadaSiYaHayUsuarios() throws Exception {
        when(usuarioOut.existeAlguno()).thenReturn(true);

        new UsuarioInicial().crearAdmin(usuarioOut, usuarioIn, "admin", "Admin2026*").run();

        verify(usuarioIn, never()).crear(any());
    }
}
