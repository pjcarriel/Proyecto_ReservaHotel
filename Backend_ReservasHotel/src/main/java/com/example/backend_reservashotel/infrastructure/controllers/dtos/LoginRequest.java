package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Usuario es obligatorio")
        String username,

        @NotBlank(message = "Password es obligatorio")
        String password
) {
}
