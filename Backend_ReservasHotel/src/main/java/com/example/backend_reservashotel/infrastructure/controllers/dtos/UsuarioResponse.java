package com.example.backend_reservashotel.infrastructure.controllers.dtos;

import com.example.backend_reservashotel.domain.models.Rol;

import java.time.LocalDateTime;

// Sin password: nunca se devuelve en la respuesta
public record UsuarioResponse(Long id, String username, String nombre, Rol rol, boolean activo,
                              LocalDateTime creado, LocalDateTime modificado) {
}
