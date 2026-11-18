package com.example.backend_reservashotel.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "huesped")
@Getter
@NoArgsConstructor
public class HuespedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 13)
    private String cedula;

    @Setter
    @Column(nullable = false, length = 100)
    private String nombres;

    @Setter
    @Column(nullable = false, length = 100)
    private String apellidos;

    @Setter
    @Column(unique = true, length = 100)
    private String email;

    @Setter
    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private LocalDateTime creado;

    @Column(nullable = false)
    private LocalDateTime modificado;

    @PrePersist
    private void alCrear() {
        this.creado = LocalDateTime.now();
        this.modificado = this.creado;
    }

    @PreUpdate
    private void alModificar() {
        this.modificado = LocalDateTime.now();
    }
}