package com.example.backend_reservashotel.infrastructure.repositories;

import com.example.backend_reservashotel.infrastructure.entities.TipoHabitacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacionEntity, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
