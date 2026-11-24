package com.example.backend_reservashotel.infrastructure.securities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class JWTFilterTests {
    private final JWTServices jwtServices = new JWTServices("8hwE1MqQ7W2sRFAqqDOJo+p2QJCJTXwxtoIex32R8bA=", 60_000);
    private final JWTFilter filtro = new JWTFilter(jwtServices);

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void conTokenValidoQuedaAutenticadoConSuRol() throws Exception {
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(peticion("Bearer " + jwtServices.generar("recepcion1", "RECEPCIONISTA")),
                new MockHttpServletResponse(), cadena);

        Authentication autenticado = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(autenticado);
        assertEquals("recepcion1", autenticado.getName());
        assertEquals("ROLE_RECEPCIONISTA", autenticado.getAuthorities().iterator().next().getAuthority());
        assertNotNull(cadena.getRequest(), "la petición debe continuar");
    }

    @Test
    void conTokenInvalidoContinuaSinAutenticar() throws Exception {
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(peticion("Bearer token.falso.123"), new MockHttpServletResponse(), cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest(), "la petición debe continuar");
    }

    @Test
    void sinTokenContinuaSinAutenticar() throws Exception {
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(peticion(null), new MockHttpServletResponse(), cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
    }

    private static MockHttpServletRequest peticion(String authorization) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/huesped");
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        return request;
    }
}
