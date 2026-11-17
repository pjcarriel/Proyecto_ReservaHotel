package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import java.time.LocalDateTime;

public record TipoHabitacionResponse(Long id, String nombre, String descripcion, int capacidad,
                                     LocalDateTime creado, LocalDateTime modificado) {
}
