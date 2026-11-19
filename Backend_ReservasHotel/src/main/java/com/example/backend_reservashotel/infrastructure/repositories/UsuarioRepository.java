package com.example.backend_reservashotel.infrastructure.repositories;

import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.infrastructure.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    // Para el login
    Optional<UsuarioEntity> findByUsernameIgnoreCase(String username);

    long countByRolAndActivoTrue(Rol rol);
}
