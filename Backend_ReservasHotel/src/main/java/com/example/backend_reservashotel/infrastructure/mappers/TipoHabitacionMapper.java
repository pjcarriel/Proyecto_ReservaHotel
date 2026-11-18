package com.example.backend_reservashotel.infrastructure.mappers;

import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.TipoHabitacionRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.TipoHabitacionResponse;
import com.example.backend_reservashotel.infrastructure.entities.TipoHabitacionEntity;

public final class TipoHabitacionMapper {
    private TipoHabitacionMapper() {
    }

    public static TipoHabitacion toDomain(TipoHabitacionEntity entity) {
        return new TipoHabitacion(entity.getId(), entity.getNombre(), entity.getDescripcion(),
                entity.getCapacidad(), entity.getCreado(), entity.getModificado());
    }

    // JPA conserva el id y gestiona las fechas de auditoria.
    public static void toEntity(TipoHabitacion tipoHabitacion, TipoHabitacionEntity entity) {
        entity.setNombre(tipoHabitacion.getNombre());
        entity.setDescripcion(tipoHabitacion.getDescripcion());
        entity.setCapacidad(tipoHabitacion.getCapacidad());
    }

    public static TipoHabitacion toRequest(TipoHabitacionRequest request) {
        return TipoHabitacion.nuevo(request.nombre(), request.descripcion(), request.capacidad());
    }

    public static TipoHabitacionResponse toResponse(TipoHabitacion tipoHabitacion) {
        return new TipoHabitacionResponse(tipoHabitacion.getId(), tipoHabitacion.getNombre(),
                tipoHabitacion.getDescripcion(), tipoHabitacion.getCapacidad(),
                tipoHabitacion.getCreado(), tipoHabitacion.getModificado());
    }
}
