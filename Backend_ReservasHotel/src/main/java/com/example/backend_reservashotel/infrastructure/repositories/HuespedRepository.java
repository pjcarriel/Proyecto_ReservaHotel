package com.example.backend_reservashotel.infrastructure.repositories;

import com.example.backend_reservashotel.infrastructure.entities.HuespedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HuespedRepository extends JpaRepository<HuespedEntity, Long> {
    boolean existsByCedulaIgnoreCase(String cedula);
    boolean existsByCedulaIgnoreCaseAndIdNot(String cedula, Long id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}