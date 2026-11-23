package com.example.backend_reservashotel.infrastructure.securities;

import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// Prueba el login completo de Spring Security (UsuarioDetailsService + BCrypt) sin base de datos
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UsuarioDetailsServiceTests {
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Mock
    private UsuarioOut usuarioOut;

    private DaoAuthenticationProvider proveedor;

    @BeforeEach
    void preparar() {
        proveedor = new DaoAuthenticationProvider(new UsuarioDetailsService(usuarioOut));
        proveedor.setPasswordEncoder(bcrypt);
        when(usuarioOut.buscarPorUsername(anyString())).thenReturn(Optional.empty());
        when(usuarioOut.buscarPorUsername("admin")).thenReturn(Optional.of(usuario("admin", Rol.ADMIN, true)));
        when(usuarioOut.buscarPorUsername("inactivo")).thenReturn(Optional.of(usuario("inactivo", Rol.RECEPCIONISTA, false)));
    }

    @Test
    void passwordCorrectoAutenticaConElRol() {
        Authentication resultado = proveedor.authenticate(login("admin", "Admin2026*"));

        assertEquals("admin", resultado.getName());
        assertEquals("ROLE_ADMIN", resultado.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void passwordIncorrectoEsRechazado() {
        assertThrows(BadCredentialsException.class, () -> proveedor.authenticate(login("admin", "otra")));
    }

    @Test
    void usuarioInexistenteDaElMismoErrorQueUnPasswordIncorrecto() {
        assertThrows(BadCredentialsException.class, () -> proveedor.authenticate(login("nadie", "Admin2026*")));
    }

    @Test
    void usuarioDesactivadoNoPuedeIniciarSesion() {
        assertThrows(DisabledException.class, () -> proveedor.authenticate(login("inactivo", "Admin2026*")));
    }

    private static UsernamePasswordAuthenticationToken login(String username, String password) {
        return UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    }

    private Usuario usuario(String username, Rol rol, boolean activo) {
        return new Usuario(1L, username, bcrypt.encode("Admin2026*"), rol, activo,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
