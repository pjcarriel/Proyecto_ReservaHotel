package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import com.example.backend_reservashotel.domain.models.Rol;

// expiraEn: milisegundos de validez del token (el frontend puede cerrar la sesión al vencer)
public record LoginResponse(String token, String username, String nombre, Rol rol, long expiraEn) {
}
