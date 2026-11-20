package com.example.backend_reservashotel.infrastructure.repositories;

import com.example.backend_reservashotel.domain.models.EstadoReserva;
import com.example.backend_reservashotel.infrastructure.entities.ReservaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"huesped", "detalles", "detalles.habitacion", "detalles.habitacion.tipoHabitacion"})
    List<ReservaEntity> findAll();

    @Override
    @EntityGraph(attributePaths = {"huesped", "detalles", "detalles.habitacion", "detalles.habitacion.tipoHabitacion"})
    Optional<ReservaEntity> findById(Long id);

    @EntityGraph(attributePaths = {"huesped", "detalles", "detalles.habitacion", "detalles.habitacion.tipoHabitacion"})
    List<ReservaEntity> findByHuespedId(Long huespedId);

    boolean existsByHuespedId(Long huespedId);

    @Query("""
            SELECT COUNT(d) > 0
            FROM DetalleReservaEntity d
            WHERE d.habitacion.id = :habitacionId
              AND d.reserva.estado = :estado
              AND d.reserva.fechaEntrada < :nuevaSalida
              AND d.reserva.fechaSalida > :nuevaEntrada
            """)
    boolean existeChoqueFechas(@Param("habitacionId") Long habitacionId,
                               @Param("estado") EstadoReserva estado,
                               @Param("nuevaEntrada") LocalDate nuevaEntrada,
                               @Param("nuevaSalida") LocalDate nuevaSalida);
}