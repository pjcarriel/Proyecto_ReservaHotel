package com.example.backend_reservashotel.infrastructure.mappers;

import com.example.backend_reservashotel.domain.models.Huesped;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.HuespedRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.HuespedResponse;
import com.example.backend_reservashotel.infrastructure.entities.HuespedEntity;

public final class HuespedMapper {

    private HuespedMapper() {
    }

    public static Huesped toDomain(HuespedEntity ety) {
        return new Huesped(
                ety.getId(),
                ety.getCedula(),
                ety.getNombres(),
                ety.getApellidos(),
                ety.getEmail(),
                ety.getTelefono(),
                ety.getCreado(),
                ety.getModificado()
        );
    }

    public static void toEntity(Huesped huespedDomain, HuespedEntity ety) {
        ety.setCedula(huespedDomain.getCedula());
        ety.setNombres(huespedDomain.getNombres());
        ety.setApellidos(huespedDomain.getApellidos());
        ety.setEmail(huespedDomain.getEmail());
        ety.setTelefono(huespedDomain.getTelefono());
    }

    public static Huesped toRequest(HuespedRequest req) {
        return Huesped.nuevo(
                req.cedula(),
                req.nombres(),
                req.apellidos(),
                req.email(),
                req.telefono()
        );
    }

    public static HuespedResponse toResponse(Huesped h) {
        return new HuespedResponse(
                h.getId(),
                h.getCedula(),
                h.getNombres(),
                h.getApellidos(),
                h.getEmail(),
                h.getTelefono(),
                h.getCreado(),
                h.getModificado()
        );
    }
}