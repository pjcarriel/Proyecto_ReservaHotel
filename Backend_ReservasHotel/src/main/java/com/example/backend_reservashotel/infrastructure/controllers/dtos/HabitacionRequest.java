package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import com.example.backend_reservashotel.domain.models.EstadoHabitacion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record HabitacionRequest(
        @NotBlank(message = "El número de habitación es obligatorio")
        @Size(max = 20, message = "El número admite máximo 20 caracteres")
        String numero,

        @NotNull(message = "El piso es obligatorio")
        @PositiveOrZero(message = "El piso no puede ser negativo")
        Integer piso,

        @NotNull(message = "El precio por noche es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio por noche debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
        BigDecimal precioNoche,

        @NotNull(message = "El estado de la habitación es obligatorio")
        EstadoHabitacion estado,

        @NotNull(message = "El tipo de habitación es obligatorio")
        @Positive(message = "El id del tipo de habitación debe ser mayor a 0")
        Long tipoHabitacionId
) {
}
