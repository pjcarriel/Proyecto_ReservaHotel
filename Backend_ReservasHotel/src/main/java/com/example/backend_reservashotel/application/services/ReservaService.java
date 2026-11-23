package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.in.ReservaIn;
import com.example.backend_reservashotel.application.ports.out.HabitacionOut;
import com.example.backend_reservashotel.application.ports.out.HuespedOut;
import com.example.backend_reservashotel.application.ports.out.ReservaOut;
import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.domain.models.Huesped;
import com.example.backend_reservashotel.domain.models.Reserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ReservaService implements ReservaIn {

    private final ReservaOut reservaOut;
    private final HuespedOut huespedOut;
    private final HabitacionOut habitacionOut;

    public ReservaService(ReservaOut reservaOut, HuespedOut huespedOut, HabitacionOut habitacionOut) {
        this.reservaOut = reservaOut;
        this.huespedOut = huespedOut;
        this.habitacionOut = habitacionOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarPorHuesped(Long huespedId) {
        if (huespedId == null) {
            return reservaOut.listar();
        }
        buscarHuesped(huespedId);
        return reservaOut.listarPorHuesped(huespedId);
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva buscarPorId(Long id) {
        if (id == null) {
            throw new DatoInvalidoException("El id de la reserva es obligatorio");
        }
        return reservaOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no existe"));
    }

    @Override
    public Reserva crear(Long huespedId, LocalDate fechaEntrada, LocalDate fechaSalida, List<Long> habitacionIds) {
        if (huespedId == null) {
            throw new DatoInvalidoException("El huésped es obligatorio");
        }
        if (habitacionIds == null || habitacionIds.isEmpty()) {
            throw new DatoInvalidoException("Debe seleccionar al menos una habitación");
        }

        Huesped huesped = buscarHuesped(huespedId);
        Reserva reserva = Reserva.nueva(huesped, fechaEntrada, fechaSalida);

        for (Long habitacionId : habitacionIds) {
            if (habitacionId == null) {
                throw new DatoInvalidoException("El id de la habitación es obligatorio");
            }
            Habitacion habitacion = buscarHabitacion(habitacionId);
            if (reservaOut.existeChoqueFechas(habitacionId, fechaEntrada, fechaSalida)) {
                throw new ReglaNegocioException(
                        "La habitación " + habitacion.getNumero() + " ya tiene una reserva confirmada en esas fechas"
                );
            }
            reserva.agregarHabitacion(habitacion);
        }

        reserva.comprobarDetalles();
        return reservaOut.guardar(reserva);
    }

    @Override
    public Reserva cancelar(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.cancelar();
        return reservaOut.guardar(reserva);
    }

    private Huesped buscarHuesped(Long id) {
        return huespedOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Huésped no existe"));
    }

    private Habitacion buscarHabitacion(Long id) {
        return habitacionOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Habitación no existe"));
    }
}