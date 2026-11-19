package com.example.backend_reservashotel.infrastructure.adapters;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordAdapterTests {
    private final PasswordAdapter adapter = new PasswordAdapter(new BCryptPasswordEncoder());

    @Test
    void encriptaConBCryptYReconoceElPasswordCorrecto() {
        String hash = adapter.encriptar("clave12345");

        assertTrue(hash.startsWith("$2"));
        assertNotEquals("clave12345", hash);
        assertTrue(adapter.coincide("clave12345", hash));
        assertFalse(adapter.coincide("otraClave1", hash));
    }

    @Test
    void elMismoPasswordGeneraHashesDistintos() {
        assertNotEquals(adapter.encriptar("clave12345"), adapter.encriptar("clave12345"));
    }
}
