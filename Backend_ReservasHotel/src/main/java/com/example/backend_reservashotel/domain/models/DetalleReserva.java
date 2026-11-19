package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetalleReserva {
    private Long id;
    private Habitacion habitacion;
    private int noches;
    private BigDecimal precioNoche;
    private BigDecimal subtotal;

    public DetalleReserva(Long id, Habitacion habitacion, int noches,
                          BigDecimal precioNoche, BigDecimal subtotal) {
        this.id = id;
        this.habitacion = habitacion;
        this.noches = noches;
        this.precioNoche = precioNoche;
        this.subtotal = subtotal;
    }

    static DetalleReserva nuevo(Habitacion habitacion, int noches) {
        if (habitacion == null) {
            throw new DatoInvalidoException("La habitación es obligatoria");
        }
        if (!habitacion.estaDisponible()) {
            throw new ReglaNegocioException("La habitación " + habitacion.getNumero() + " no está disponible");
        }
        if (noches <= 0) {
            throw new DatoInvalidoException("El número de noches debe ser mayor a 0");
        }
        BigDecimal precio = habitacion.getPrecioNoche();
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(noches))
                .setScale(2, RoundingMode.HALF_UP);
        return new DetalleReserva(null, habitacion, noches, precio, subtotal);
    }

    public Long getId() {
        return id;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public int getNoches() {
        return noches;
    }

    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}