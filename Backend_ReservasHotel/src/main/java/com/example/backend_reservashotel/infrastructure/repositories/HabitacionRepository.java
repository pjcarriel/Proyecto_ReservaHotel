package com.example.backend_reservashotel.infrastructure.repositories;

import com.example.backend_reservashotel.infrastructure.entities.HabitacionEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<HabitacionEntity, Long> {
    @Override
    @EntityGraph(attributePaths = "tipoHabitacion")
    List<HabitacionEntity> findAll();

    @Override
    @EntityGraph(attributePaths = "tipoHabitacion")
    Optional<HabitacionEntity> findById(Long id);

    boolean existsByNumeroIgnoreCase(String numero);

    boolean existsByNumeroIgnoreCaseAndIdNot(String numero, Long id);

    boolean existsByTipoHabitacionId(Long tipoHabitacionId);
}
