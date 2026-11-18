package com.example.backend_reservashotel.infrastructure.mappers;

import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.UsuarioRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.UsuarioResponse;
import com.example.backend_reservashotel.infrastructure.entities.UsuarioEntity;

public final class UsuarioMapper {
    private UsuarioMapper() {
    }

    public static Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(entity.getId(), entity.getUsername(), entity.getNombre(), entity.getPassword(),
                entity.getRol(), entity.isActivo(), entity.getCreado(), entity.getModificado());
    }

    // JPA conserva el id y gestiona las fechas de auditoría
    public static void toEntity(Usuario usuario, UsuarioEntity entity) {
        entity.setUsername(usuario.getUsername());
        entity.setNombre(usuario.getNombre());
        entity.setPassword(usuario.getPassword());
        entity.setRol(usuario.getRol());
        entity.setActivo(usuario.isActivo());
    }

    // El password va en texto plano hasta el servicio, que lo encripta
    public static Usuario toRequest(UsuarioRequest request) {
        return Usuario.nuevo(request.username(), request.nombre(), request.password(), request.rol());
    }

    // El password NO se envía en la respuesta
    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getNombre(),
                usuario.getRol(), usuario.isActivo(), usuario.getCreado(), usuario.getModificado());
    }
}
