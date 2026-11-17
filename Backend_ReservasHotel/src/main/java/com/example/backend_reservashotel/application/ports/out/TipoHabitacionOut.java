package com.example.backend_reservashotel.application.ports.out;

import com.example.backend_reservashotel.domain.models.TipoHabitacion;

import java.util.List;
import java.util.Optional;

// Contrato de persistencia que implementara el adaptador de tipos de habitacion.
public interface TipoHabitacionOut {
    List<TipoHabitacion> listar();

    TipoHabitacion guardar(TipoHabitacion tipoHabitacion);

    void eliminar(Long id);

    Optional<TipoHabitacion> buscarPorId(Long id);

    // Al crear, idExcluir es null; al actualizar, es el id del tipo editado.
    boolean existePorNombre(String nombre, Long idExcluir);
}
