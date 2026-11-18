package com.example.backend_reservashotel.infrastructure.adapters;

import com.example.backend_reservashotel.application.ports.out.HuespedOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.models.Huesped;
import com.example.backend_reservashotel.infrastructure.entities.HuespedEntity;
import com.example.backend_reservashotel.infrastructure.mappers.HuespedMapper;
import com.example.backend_reservashotel.infrastructure.repositories.HuespedRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HuespedAdapter implements HuespedOut {

    private final HuespedRepository huespedRepository;

    public HuespedAdapter(HuespedRepository huespedRepository) {
        this.huespedRepository = huespedRepository;
    }

    @Override
    public List<Huesped> listar() {
        return huespedRepository.findAll().stream()
                .map(HuespedMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Huesped> buscarPorId(Long id) {
        return huespedRepository.findById(id).map(HuespedMapper::toDomain);
    }

    @Override
    public Huesped guardar(Huesped huesped) {
        HuespedEntity entity = huesped.getId() == null
                ? new HuespedEntity()
                : huespedRepository.findById(huesped.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Huésped no encontrado"));
        HuespedMapper.toEntity(huesped, entity);
        return HuespedMapper.toDomain(huespedRepository.saveAndFlush(entity));
    }

    @Override
    public void eliminar(Long id) {
        huespedRepository.deleteById(id);
        huespedRepository.flush();
    }

    @Override
    public boolean existePorId(Long id) {
        return huespedRepository.existsById(id);
    }

    @Override
    public boolean existePorCedula(String cedula, Long id) {
        return id == null
                ? huespedRepository.existsByCedulaIgnoreCase(cedula)
                : huespedRepository.existsByCedulaIgnoreCaseAndIdNot(cedula, id);
    }

    @Override
    public boolean existePorEmail(String email, Long id) {
        return id == null
                ? huespedRepository.existsByEmailIgnoreCase(email)
                : huespedRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }
}