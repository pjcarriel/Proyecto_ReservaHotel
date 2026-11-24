package com.example.backend_reservashotel.infrastructure.adapters;

import com.example.backend_reservashotel.application.ports.out.ReservaOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.models.EstadoReserva;
import com.example.backend_reservashotel.domain.models.Reserva;
import com.example.backend_reservashotel.infrastructure.entities.ReservaEntity;
import com.example.backend_reservashotel.infrastructure.mappers.ReservaMapper;
import com.example.backend_reservashotel.infrastructure.repositories.HabitacionRepository;
import com.example.backend_reservashotel.infrastructure.repositories.HuespedRepository;
import com.example.backend_reservashotel.infrastructure.repositories.ReservaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ReservaAdapter implements ReservaOut {

    private final ReservaRepository reservaRepository;
    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;

    public ReservaAdapter(ReservaRepository reservaRepository,
                          HuespedRepository huespedRepository,
                          HabitacionRepository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.huespedRepository = huespedRepository;
        this.habitacionRepository = habitacionRepository;
    }

    @Override
    public List<Reserva> listar() {
        return reservaRepository.findAll().stream()
                .map(ReservaMapper::toDomain)
                .toList();
    }

    @Override
    public List<Reserva> listarPorHuesped(Long huespedId) {
        return reservaRepository.findByHuespedId(huespedId).stream()
                .map(ReservaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id).map(ReservaMapper::toDomain);
    }

    @Override
    public Reserva guardar(Reserva reserva) {
        if (reserva.getId() == null) {
            ReservaEntity entity = ReservaMapper.toEntity(
                    reserva,
                    huespedRepository.getReferenceById(reserva.getHuesped().getId()),
                    habitacionRepository::getReferenceById
            );
            ReservaEntity guardada = reservaRepository.saveAndFlush(entity);
            return reservaRepository.findById(guardada.getId())
                    .map(ReservaMapper::toDomain)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no existe"));
        }

        ReservaEntity existente = reservaRepository.findById(reserva.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no existe"));
        existente.setEstado(reserva.getEstado());
        ReservaEntity actualizada = reservaRepository.saveAndFlush(existente);
        return reservaRepository.findById(actualizada.getId())
                .map(ReservaMapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no existe"));
    }

    @Override
    public boolean existePorHuesped(Long huespedId) {
        return reservaRepository.existsByHuespedId(huespedId);
    }

    @Override
    public boolean existeChoqueFechas(Long habitacionId, LocalDate fechaEntrada, LocalDate fechaSalida) {
        return reservaRepository.existeChoqueFechas(
                habitacionId,
                EstadoReserva.CONFIRMADA,
                fechaEntrada,
                fechaSalida
        );
    }
}