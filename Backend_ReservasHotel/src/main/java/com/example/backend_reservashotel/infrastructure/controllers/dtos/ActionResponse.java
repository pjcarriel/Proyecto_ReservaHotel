package com.example.backend_reservashotel.infrastructure.controllers.dtos;

// Respuesta de una accion (crear, actualizar, eliminar): mensaje + resultado
public record ActionResponse<T>(String message, T result) {
}
