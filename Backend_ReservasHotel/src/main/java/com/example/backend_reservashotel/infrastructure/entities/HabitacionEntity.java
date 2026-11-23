package com.example.backend_reservashotel.infrastructure.entities;

import com.example.backend_reservashotel.domain.models.EstadoHabitacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "habitacion")
@Getter
@NoArgsConstructor
public class HabitacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Setter
    @Column(nullable = false)
    private int piso;

    @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNoche;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoHabitacion estado;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_habitacion_tipo"))
    private TipoHabitacionEntity tipoHabitacion;

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
