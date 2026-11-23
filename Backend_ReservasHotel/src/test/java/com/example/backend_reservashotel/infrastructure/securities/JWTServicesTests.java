package com.example.backend_reservashotel.infrastructure.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JWTServicesTests {
    private static final String CLAVE = "8hwE1MqQ7W2sRFAqqDOJo+p2QJCJTXwxtoIex32R8bA=";
    private static final String OTRA_CLAVE = "c2VndW5kYUNsYXZlRGVQcnVlYmFzUGFyYUp3dDEyMzQ=";

    @Test
    void generaUnTokenQueSePuedeLeer() {
        JWTServices jwt = new JWTServices(CLAVE, 60_000);

        Claims datos = jwt.leer(jwt.generar("admin", "ADMIN"));

        assertEquals("admin", datos.getSubject());
        assertEquals("ADMIN", datos.get("rol", String.class));
    }

    @Test
    void rechazaUnTokenFirmadoConOtraClave() {
        String token = new JWTServices(OTRA_CLAVE, 60_000).generar("admin", "ADMIN");

        assertThrows(JwtException.class, () -> new JWTServices(CLAVE, 60_000).leer(token));
    }

    @Test
    void rechazaUnTokenExpirado() {
        JWTServices jwt = new JWTServices(CLAVE, -1_000);

        String token = jwt.generar("admin", "ADMIN");

        assertThrows(ExpiredJwtException.class, () -> jwt.leer(token));
    }
}
