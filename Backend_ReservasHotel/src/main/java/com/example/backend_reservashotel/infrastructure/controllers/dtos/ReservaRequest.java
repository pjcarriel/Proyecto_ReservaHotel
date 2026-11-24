package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ReservaRequest(
        @NotNull(message = "El huésped es obligatorio")
        Long huespedId,

        @NotNull(message = "La fecha de entrada es obligatoria")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaSalida,

        @NotEmpty(message = "La reserva debe tener al menos una habitación")
        List<@NotNull(message = "El id de la habitación es obligatorio") Long> habitacionIds
) {
}