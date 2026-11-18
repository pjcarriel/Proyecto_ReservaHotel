package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import java.time.LocalDateTime;

public record HuespedResponse(
        Long id,
        String cedula,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        LocalDateTime creado,
        LocalDateTime modificado
) {
}