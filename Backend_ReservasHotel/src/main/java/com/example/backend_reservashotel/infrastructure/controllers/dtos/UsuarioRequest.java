package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import com.example.backend_reservashotel.domain.models.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 4, max = 30, message = "El username debe tener entre 4 y 30 caracteres")
        @Pattern(regexp = "^\\s*[A-Za-z0-9._-]+\\s*$",
                message = "El username solo admite letras, números, punto, guion y guion bajo")
        String username,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
        String nombre,

        // Obligatorio al crear (8 a 72 caracteres). Al actualizar, vacío = conservar el actual.
        // La longitud se valida en UsuarioService.
        String password,

        // Valores permitidos: ADMIN, RECEPCIONISTA
        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {
}
