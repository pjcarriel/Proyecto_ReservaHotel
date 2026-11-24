package com.example.backend_reservashotel.infrastructure.adapters;

import com.example.backend_reservashotel.application.ports.out.PasswordOut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
// Implementa el puerto PasswordOut usando BCrypt (PasswordEncoder de SecurityConfig)
public class PasswordAdapter implements PasswordOut {

    private final PasswordEncoder passwordEncoder;

    public PasswordAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encriptar(String passwordPlano) {
        return passwordEncoder.encode(passwordPlano);
    }

    @Override
    public boolean coincide(String passwordPlano, String passwordEncriptado) {
        return passwordEncoder.matches(passwordPlano, passwordEncriptado);
    }
}
