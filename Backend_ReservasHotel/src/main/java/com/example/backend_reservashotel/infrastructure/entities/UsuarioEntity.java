package com.example.backend_reservashotel.infrastructure.entities;

import com.example.backend_reservashotel.domain.models.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "usuario")
@Getter
@NoArgsConstructor
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Setter
    @Column(nullable = false, length = 100)
    private String nombre;

    // Hash BCrypt (60 caracteres)
    @Setter
    @Column(nullable = false, length = 100)
    private String password;

    // Se guarda el nombre del rol (ADMIN, RECEPCIONISTA) y no su posición
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Setter
    @Column(nullable = false)
    private boolean activo;

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
