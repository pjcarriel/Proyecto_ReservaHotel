package com.example.backend_reservashotel.infrastructure.adapters;

import com.example.backend_reservashotel.application.ports.out.HabitacionOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.infrastructure.entities.HabitacionEntity;
import com.example.backend_reservashotel.infrastructure.entities.TipoHabitacionEntity;
import com.example.backend_reservashotel.infrastructure.mappers.HabitacionMapper;
import com.example.backend_reservashotel.infrastructure.repositories.HabitacionRepository;
import com.example.backend_reservashotel.infrastructure.repositories.TipoHabitacionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HabitacionAdapter implements HabitacionOut {
    private final HabitacionRepository habitacionRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;

    public HabitacionAdapter(HabitacionRepository habitacionRepository, TipoHabitacionRepository tipoHabitacionRepository) {
        this.habitacionRepository = habitacionRepository;
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    @Override
    public List<Habitacion> listar() {
        return habitacionRepository.findAll().stream().map(HabitacionMapper::toDomain).toList();
    }

    @Override
    public Optional<Habitacion> buscarPorId(Long id) {
        return habitacionRepository.findById(id).map(HabitacionMapper::toDomain);
    }

    @Override
    public Habitacion guardar(Habitacion habitacion) {
        HabitacionEntity entity = habitacion.getId() == null
                ? new HabitacionEntity()
                : habitacionRepository.findById(habitacion.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe la habitación"));
        TipoHabitacionEntity tipo = tipoHabitacionRepository.findById(habitacion.getTipoHabitacion().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de habitación"));
        HabitacionMapper.toEntity(habitacion, entity, tipo);
        try {
            return HabitacionMapper.toDomain(habitacionRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se pudo guardar la habitación; verifica que el número sea único y el tipo sea válido");
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            habitacionRepository.deleteById(id);
            habitacionRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se puede eliminar la habitación porque tiene registros asociados");
        }
    }

    @Override
    public boolean existePorNumero(String numero, Long idExcluir) {
        return idExcluir == null
                ? habitacionRepository.existsByNumeroIgnoreCase(numero)
                : habitacionRepository.existsByNumeroIgnoreCaseAndIdNot(numero, idExcluir);
    }

    @Override
    public boolean existePorTipoHabitacion(Long tipoHabitacionId) {
        return habitacionRepository.existsByTipoHabitacionId(tipoHabitacionId);
    }
}
