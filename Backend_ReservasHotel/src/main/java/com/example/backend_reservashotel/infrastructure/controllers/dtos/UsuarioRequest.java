package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import com.example.backend_reservashotel.domain.models.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 4, max = 30, message = "El username debe tener entre 4 y 30 caracteres")
        String username,

        @NotBlank(message = "El password es obligatorio")
        @Size(min = 8, max = 100, message = "El password debe tener al menos 8 caracteres")
        String password,

        // Valores permitidos: ADMIN, RECEPCIONISTA
        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {
}
