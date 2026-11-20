package com.example.backend_reservashotel.application.ports.out;

import com.example.backend_reservashotel.domain.models.Reserva;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaOut {
    List<Reserva> listar();

    List<Reserva> listarPorHuesped(Long huespedId);

    Optional<Reserva> buscarPorId(Long id);

    Reserva guardar(Reserva reserva);

    boolean existePorHuesped(Long huespedId);

    // Comprueba si la habitacion ya tiene otra reserva CONFIRMADA que se cruce con las fechas dadas.
    boolean existeChoqueFechas(Long habitacionId, LocalDate fechaEntrada, LocalDate fechaSalida);
}