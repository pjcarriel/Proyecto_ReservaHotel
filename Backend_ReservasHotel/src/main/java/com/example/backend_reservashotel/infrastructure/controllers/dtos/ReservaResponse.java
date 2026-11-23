package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import com.example.backend_reservashotel.domain.models.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReservaResponse(
        Long id,
        String numero,
        Long huespedId,
        String huespedCedula,
        String huespedNombre,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        EstadoReserva estado,
        List<Item> detalles,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total,
        LocalDateTime creado,
        LocalDateTime modificado
) {
    public record Item(
            Long habitacionId,
            String habitacionNumero,
            String tipoHabitacionNombre,
            int noches,
            BigDecimal precioNoche,
            BigDecimal subtotal
    ) {
    }
}