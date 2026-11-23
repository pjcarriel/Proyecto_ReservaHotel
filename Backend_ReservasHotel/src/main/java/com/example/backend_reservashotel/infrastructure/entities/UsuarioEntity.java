package com.example.backend_reservashotel.infrastructure.entities;

import com.example.backend_reservashotel.domain.models.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private String password;

    // Se guarda el nombre del rol (ADMIN, RECEPCIONISTA) y no su posicion
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Setter
    @Column(nullable = false)
    private boolean activo;

    //Campos de Auditoria
    @Column(nullable = false)
    private LocalDateTime creado;

    @Column(nullable = false)
    private LocalDateTime modificado;

    @PrePersist
    private void alCrear(){
        this.creado = LocalDateTime.now();
        this.modificado = this.creado;
    }
    @PreUpdate
    private void alModificar(){
        this.modificado = LocalDateTime.now();
    }
}
