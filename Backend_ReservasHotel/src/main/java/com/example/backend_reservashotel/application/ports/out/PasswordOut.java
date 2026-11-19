package com.example.backend_reservashotel.application.ports.out;

// Encriptación de passwords. La implementa PasswordAdapter con BCrypt.
public interface PasswordOut {
    String encriptar(String passwordPlano);

    // Compara el password plano con el hash guardado (para el login)
    boolean coincide(String passwordPlano, String passwordEncriptado);
}
