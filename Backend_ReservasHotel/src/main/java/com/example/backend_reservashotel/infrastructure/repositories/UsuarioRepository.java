package com.example.backend_reservashotel.infrastructure.repositories;

import com.example.backend_reservashotel.infrastructure.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    // FUNCIONES ADICIONALES, se puede agregar procesos de busqueda

    // Username unico: al crear (sin id) y al actualizar (excluyendo al mismo usuario)
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    // Para el login
    Optional<UsuarioEntity> findByUsername(String username);
}
