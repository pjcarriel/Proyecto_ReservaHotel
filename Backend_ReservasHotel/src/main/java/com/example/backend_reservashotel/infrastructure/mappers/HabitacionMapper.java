package com.example.backend_reservashotel.infrastructure.mappers;

import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.HabitacionRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.HabitacionResponse;
import com.example.backend_reservashotel.infrastructure.entities.HabitacionEntity;
import com.example.backend_reservashotel.infrastructure.entities.TipoHabitacionEntity;

public final class HabitacionMapper {
    private HabitacionMapper() {
    }

    public static Habitacion toDomain(HabitacionEntity entity) {
        return new Habitacion(entity.getId(), entity.getNumero(), entity.getPiso(), entity.getPrecioNoche(),
                entity.getEstado(), TipoHabitacionMapper.toDomain(entity.getTipoHabitacion()),
                entity.getCreado(), entity.getModificado());
    }

    public static void toEntity(Habitacion habitacion, HabitacionEntity entity, TipoHabitacionEntity tipo) {
        entity.setNumero(habitacion.getNumero());
        entity.setPiso(habitacion.getPiso());
        entity.setPrecioNoche(habitacion.getPrecioNoche());
        entity.setEstado(habitacion.getEstado());
        entity.setTipoHabitacion(tipo);
    }

    // El servicio resuelve el tipo por su id y valida el dominio antes de guardar.
    public static Habitacion toRequest(HabitacionRequest request) {
        return new Habitacion(null, request.numero(), request.piso(), request.precioNoche(),
                request.estado(), null, null, null);
    }

    public static HabitacionResponse toResponse(Habitacion habitacion) {
        return new HabitacionResponse(habitacion.getId(), habitacion.getNumero(), habitacion.getPiso(),
                habitacion.getPrecioNoche(), habitacion.getEstado(),
                habitacion.getTipoHabitacion().getId(), habitacion.getTipoHabitacion().getNombre(),
                habitacion.getCreado(), habitacion.getModificado());
    }
}
