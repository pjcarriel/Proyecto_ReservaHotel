package com.example.backend_reservashotel.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tipo_habitacion")
@Getter
@NoArgsConstructor
public class TipoHabitacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    @Setter
    @Column(nullable = false, length = 200)
    private String descripcion;

    @Setter
    @Column(nullable = false)
    private int capacidad;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creado;

    @Column(nullable = false)
    private LocalDateTime modificado;

    @PrePersist
    private void alCrear() {
        // MySQL DATETIME(6) almacena microsegundos; evitar cambios de precision al consultar.
        this.creado = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        this.modificado = this.creado;
    }

    @PreUpdate
    private void alModificar() {
        this.modificado = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}
