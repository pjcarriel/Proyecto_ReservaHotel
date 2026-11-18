package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Habitacion {
    private Long id;
    private String numero;
    private int piso;
    private BigDecimal precioNoche;
    private EstadoHabitacion estado;
    private TipoHabitacion tipoHabitacion;
    private LocalDateTime creado;
    private LocalDateTime modificado;

    // Constructor completo para reconstruir el dominio desde la persistencia.
    public Habitacion(Long id, String numero, int piso, BigDecimal precioNoche,
                      EstadoHabitacion estado, TipoHabitacion tipoHabitacion,
                      LocalDateTime creado, LocalDateTime modificado) {
        this.id = id;
        this.numero = numero;
        this.piso = piso;
        this.precioNoche = precioNoche;
        this.estado = estado;
        this.tipoHabitacion = tipoHabitacion;
        this.creado = creado;
        this.modificado = modificado;
    }

    public static Habitacion nuevo(String numero, int piso, BigDecimal precioNoche,
                                   EstadoHabitacion estado, TipoHabitacion tipoHabitacion) {
        validarDatosBasicos(numero, precioNoche, estado, tipoHabitacion);
        return new Habitacion(null, numero.trim(), piso, precioNoche, estado,
                tipoHabitacion, null, null);
    }

    public void actualizar(String numero, int piso, BigDecimal precioNoche,
                           EstadoHabitacion estado, TipoHabitacion tipoHabitacion) {
        validarDatosBasicos(numero, precioNoche, estado, tipoHabitacion);
        this.numero = numero.trim();
        this.piso = piso;
        this.precioNoche = precioNoche;
        this.estado = estado;
        this.tipoHabitacion = tipoHabitacion;
    }

    private static void validarDatosBasicos(String numero, BigDecimal precioNoche,
                                           EstadoHabitacion estado, TipoHabitacion tipoHabitacion) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new DatoInvalidoException("El número de habitación es obligatorio");
        }
        if (precioNoche == null || precioNoche.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatoInvalidoException("El precio por noche debe ser mayor a 0");
        }
        if (estado == null) {
            throw new DatoInvalidoException("El estado de la habitación es obligatorio");
        }
        if (tipoHabitacion == null) {
            throw new DatoInvalidoException("El tipo de habitación es obligatorio");
        }
    }

    public boolean estaDisponible() {
        return estado == EstadoHabitacion.DISPONIBLE;
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public int getPiso() {
        return piso;
    }

    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public LocalDateTime getCreado() {
        return creado;
    }

    public LocalDateTime getModificado() {
        return modificado;
    }
}
