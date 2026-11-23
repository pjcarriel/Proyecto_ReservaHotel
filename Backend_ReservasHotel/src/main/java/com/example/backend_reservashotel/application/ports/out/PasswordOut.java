package com.example.backend_reservashotel.application.ports.out;

public interface PasswordOut {
    String encriptar(String passwordPlano);

    // Compara el password plano con el hash guardado
    boolean coincide(String passwordPlano, String passwordEncriptado);
}
