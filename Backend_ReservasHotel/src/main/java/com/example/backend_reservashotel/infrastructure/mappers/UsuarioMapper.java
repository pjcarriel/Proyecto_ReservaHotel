package com.example.backend_reservashotel.infrastructure.mappers;

import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.UsuarioRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.UsuarioResponse;
import com.example.backend_reservashotel.infrastructure.entities.UsuarioEntity;

public final class UsuarioMapper {

    private UsuarioMapper() {}

    // Entity -> Dominio
    public static Usuario toDomain(UsuarioEntity ety) {
        return new Usuario(ety.getId(), ety.getUsername(), ety.getPassword(), ety.getRol(),
                ety.isActivo(), ety.getCreado(), ety.getModificado());
    }

    // Dominio -> Entity
    // Recibe la entity nueva o la ya existente (id y auditoria los gestiona JPA)
    public static void toEntity(Usuario usuarioDomain, UsuarioEntity ety) {
        ety.setUsername(usuarioDomain.getUsername());
        ety.setPassword(usuarioDomain.getPassword());
        ety.setRol(usuarioDomain.getRol());
        ety.setActivo(usuarioDomain.isActivo());
    }

    public static Usuario toRequest(UsuarioRequest usuReq){
        return Usuario.nuevo(usuReq.username(), usuReq.password(), usuReq.rol());
    }

    // El password NO se envia en la respuesta
    public static UsuarioResponse toResponse(Usuario u){
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getRol(), u.isActivo(),
                u.getCreado(), u.getModificado());
    }
}
