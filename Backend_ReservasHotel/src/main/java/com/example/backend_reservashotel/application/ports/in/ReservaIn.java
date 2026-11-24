package com.example.backend_reservashotel.application.ports.in;

import com.example.backend_reservashotel.domain.models.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaIn {
    List<Reserva> listarPorHuesped(Long huespedId);

    Reserva buscarPorId(Long id);

    Reserva crear(Long huespedId, LocalDate fechaEntrada, LocalDate fechaSalida, List<Long> habitacionIds);

    Reserva cancelar(Long id);
}