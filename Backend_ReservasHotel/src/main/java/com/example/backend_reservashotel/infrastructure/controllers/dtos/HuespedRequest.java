package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HuespedRequest(
        @NotBlank(message = "La cédula es obligatoria")
        @Size(max = 13, message = "La cédula admite máximo 13 caracteres")
        String cedula,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 60, message = "Los nombres admiten máximo 60 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 60, message = "Los apellidos admiten máximo 60 caracteres")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 100, message = "El email admite máximo 100 caracteres")
        String email,

        @Size(max = 20, message = "El teléfono admite máximo 20 caracteres")
        String telefono
) {
}