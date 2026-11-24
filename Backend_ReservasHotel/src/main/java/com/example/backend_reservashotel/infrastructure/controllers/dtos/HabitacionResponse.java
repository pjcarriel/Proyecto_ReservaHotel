package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import com.example.backend_reservashotel.domain.models.EstadoHabitacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HabitacionResponse(Long id, String numero, int piso, BigDecimal precioNoche,
                                  EstadoHabitacion estado, Long tipoHabitacionId, String tipoHabitacionNombre,
                                  LocalDateTime creado, LocalDateTime modificado) {
}
