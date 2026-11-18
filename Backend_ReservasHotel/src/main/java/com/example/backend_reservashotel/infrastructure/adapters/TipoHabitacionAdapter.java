package com.example.backend_reservashotel.infrastructure.adapters;

import com.example.backend_reservashotel.application.ports.out.TipoHabitacionOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import com.example.backend_reservashotel.infrastructure.entities.TipoHabitacionEntity;
import com.example.backend_reservashotel.infrastructure.mappers.TipoHabitacionMapper;
import com.example.backend_reservashotel.infrastructure.repositories.TipoHabitacionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TipoHabitacionAdapter implements TipoHabitacionOut {
    private final TipoHabitacionRepository tipoHabitacionRepository;

    public TipoHabitacionAdapter(TipoHabitacionRepository tipoHabitacionRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    @Override
    public List<TipoHabitacion> listar() {
        return tipoHabitacionRepository.findAll().stream()
                .map(TipoHabitacionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<TipoHabitacion> buscarPorId(Long id) {
        return tipoHabitacionRepository.findById(id).map(TipoHabitacionMapper::toDomain);
    }

    @Override
    public TipoHabitacion guardar(TipoHabitacion tipoHabitacion) {
        TipoHabitacionEntity entity = tipoHabitacion.getId() == null
                ? new TipoHabitacionEntity()
                : tipoHabitacionRepository.findById(tipoHabitacion.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de habitación"));
        TipoHabitacionMapper.toEntity(tipoHabitacion, entity);
        try {
            return TipoHabitacionMapper.toDomain(tipoHabitacionRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            // La restriccion UNIQUE tambien protege frente a creaciones simultaneas.
            throw new ReglaNegocioException("No se pudo guardar el tipo de habitación; verifica que el nombre sea único y los datos sean válidos");
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            tipoHabitacionRepository.deleteById(id);
            tipoHabitacionRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se puede eliminar el tipo de habitación porque tiene registros asociados");
        }
    }

    @Override
    public boolean existePorNombre(String nombre, Long idExcluir) {
        return idExcluir == null
                ? tipoHabitacionRepository.existsByNombreIgnoreCase(nombre)
                : tipoHabitacionRepository.existsByNombreIgnoreCaseAndIdNot(nombre, idExcluir);
    }
}
