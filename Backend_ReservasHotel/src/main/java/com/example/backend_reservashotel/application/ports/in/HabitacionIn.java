package com.example.backend_reservashotel.application.ports.in;

import com.example.backend_reservashotel.domain.models.Habitacion;

import java.util.List;

// Operaciones que ofrecera el servicio de habitaciones.
public interface HabitacionIn {
    List<Habitacion> listar();

    // El servicio buscara el tipo de habitacion usando tipoHabitacionId.
    Habitacion crear(Habitacion habitacion, Long tipoHabitacionId);

    Habitacion actualizar(Long id, Habitacion habitacion, Long tipoHabitacionId);

    void eliminar(Long id);

    Habitacion buscarPorId(Long id);
}
