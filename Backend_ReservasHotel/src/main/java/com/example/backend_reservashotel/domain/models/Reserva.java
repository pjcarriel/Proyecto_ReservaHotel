package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    public static final BigDecimal IVA = new BigDecimal("0.15");

    private Long id;
    private String numero;
    private Huesped huesped;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private EstadoReserva estado;
    private List<DetalleReserva> detalles;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private LocalDateTime creado;
    private LocalDateTime modificado;

    public Reserva(Long id, String numero, Huesped huesped, LocalDate fechaEntrada, LocalDate fechaSalida,
                   EstadoReserva estado, List<DetalleReserva> detalles,
                   BigDecimal subtotal, BigDecimal iva, BigDecimal total,
                   LocalDateTime creado, LocalDateTime modificado) {
        this.id = id;
        this.numero = numero;
        this.huesped = huesped;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
        this.detalles = detalles != null ? new ArrayList<>(detalles) : new ArrayList<>();
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.creado = creado;
        this.modificado = modificado;
    }

    public static Reserva nueva(Huesped huesped, LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (huesped == null) {
            throw new DatoInvalidoException("La reserva debe tener un huésped");
        }
        if (fechaEntrada == null) {
            throw new DatoInvalidoException("La fecha de entrada es obligatoria");
        }
        if (fechaSalida == null) {
            throw new DatoInvalidoException("La fecha de salida es obligatoria");
        }
        if (fechaEntrada.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de entrada no puede estar en el pasado");
        }
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new DatoInvalidoException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        String numero = "RES-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        BigDecimal cero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return new Reserva(
                null,
                numero,
                huesped,
                fechaEntrada,
                fechaSalida,
                EstadoReserva.CONFIRMADA,
                new ArrayList<>(),
                cero,
                cero,
                cero,
                null,
                null
        );
    }

    public int calcularNoches() {
        return (int) ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    }

    public void agregarHabitacion(Habitacion habitacion) {
        if (habitacion == null) {
            throw new DatoInvalidoException("La habitación es obligatoria");
        }
        boolean yaAgregada = detalles.stream()
                .anyMatch(d -> d.getHabitacion().getId() != null
                        && d.getHabitacion().getId().equals(habitacion.getId()));
        if (yaAgregada) {
            throw new DatoInvalidoException("La habitación " + habitacion.getNumero() + " está repetida en la reserva");
        }
        detalles.add(DetalleReserva.nuevo(habitacion, calcularNoches()));
        calcularTotal();
    }

    public void comprobarDetalles() {
        if (detalles.isEmpty()) {
            throw new DatoInvalidoException("La reserva debe tener al menos una habitación");
        }
    }

    public void cancelar() {
        if (this.estado == EstadoReserva.CANCELADA) {
            throw new ReglaNegocioException("La reserva ya se encuentra cancelada");
        }
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean estaConfirmada() {
        return this.estado == EstadoReserva.CONFIRMADA;
    }

    private void calcularTotal() {
        this.subtotal = detalles.stream()
                .map(DetalleReserva::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        this.iva = subtotal.multiply(IVA).setScale(2, RoundingMode.HALF_UP);
        this.total = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public List<DetalleReserva> getDetalles() {
        return detalles;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getCreado() {
        return creado;
    }

    public LocalDateTime getModificado() {
        return modificado;
    }
}