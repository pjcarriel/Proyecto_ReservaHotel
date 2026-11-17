package com.example.backend_reservashotel.application.ports.in;

import com.example.backend_reservashotel.domain.models.TipoHabitacion;

import java.util.List;

// Operaciones que ofrecera el servicio de tipos de habitacion.
public interface TipoHabitacionIn {
    List<TipoHabitacion> listar();

    TipoHabitacion crear(TipoHabitacion tipoHabitacion);

    TipoHabitacion actualizar(Long id, TipoHabitacion tipoHabitacion);

    void eliminar(Long id);

    TipoHabitacion buscarPorId(Long id);
}
