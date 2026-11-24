package com.example.backend_reservashotel.infrastructure.entities;

import com.example.backend_reservashotel.domain.models.EstadoReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reserva")
@Getter
@NoArgsConstructor
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 25)
    private String numero;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "huesped_id", nullable = false)
    private HuespedEntity huesped;

    @Setter
    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Setter
    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @Setter
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleReservaEntity> detalles = new ArrayList<>();

    @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal iva;

    @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creado;

    @Column(nullable = false)
    private LocalDateTime modificado;

    @PrePersist
    private void alCrear() {
        this.creado = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        this.modificado = this.creado;
    }

    @PreUpdate
    private void alModificar() {
        this.modificado = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}