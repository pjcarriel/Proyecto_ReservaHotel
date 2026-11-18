package com.example.backend_reservashotel.application.ports.out;

import com.example.backend_reservashotel.domain.models.Habitacion;

import java.util.List;
import java.util.Optional;

// Contrato de persistencia que usaran los servicios de habitaciones y reservas.
public interface HabitacionOut {
    List<Habitacion> listar();

    Habitacion guardar(Habitacion habitacion);

    void eliminar(Long id);

    // Permite consultar la habitacion y su precio por noche desde ReservaService.
    Optional<Habitacion> buscarPorId(Long id);

    // Al crear, idExcluir es null; al actualizar, es el id de la habitacion editada.
    boolean existePorNumero(String numero, Long idExcluir);

    // Permite comprobar si un tipo de habitacion tiene habitaciones asociadas.
    boolean existePorTipoHabitacion(Long tipoHabitacionId);
}
