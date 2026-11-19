package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotNull;

// Body del PATCH: {"activo": true} o {"activo": false}
public record EstadoUsuarioRequest(
        @NotNull(message = "El campo activo es obligatorio")
        Boolean activo
) {
}
