package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.in.TipoHabitacionIn;
import com.example.backend_reservashotel.application.ports.out.TipoHabitacionOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TipoHabitacionService implements TipoHabitacionIn {
    private final TipoHabitacionOut tipoHabitacionOut;

    public TipoHabitacionService(TipoHabitacionOut tipoHabitacionOut) {
        this.tipoHabitacionOut = tipoHabitacionOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoHabitacion> listar() {
        return tipoHabitacionOut.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoHabitacion buscarPorId(Long id) {
        return tipoHabitacionOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de habitación"));
    }

    @Override
    public TipoHabitacion crear(TipoHabitacion tipoHabitacion) {
        TipoHabitacion nuevo = TipoHabitacion.nuevo(tipoHabitacion.getNombre(),
                tipoHabitacion.getDescripcion(), tipoHabitacion.getCapacidad());
        validarNombreUnico(nuevo.getNombre(), null);
        return tipoHabitacionOut.guardar(nuevo);
    }

    @Override
    public TipoHabitacion actualizar(Long id, TipoHabitacion tipoHabitacion) {
        TipoHabitacion actual = buscarPorId(id);
        TipoHabitacion datos = TipoHabitacion.nuevo(tipoHabitacion.getNombre(),
                tipoHabitacion.getDescripcion(), tipoHabitacion.getCapacidad());
        validarNombreUnico(datos.getNombre(), id);
        actual.actualizar(datos.getNombre(), datos.getDescripcion(), datos.getCapacidad());
        return tipoHabitacionOut.guardar(actual);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id);
        tipoHabitacionOut.eliminar(id);
    }

    private void validarNombreUnico(String nombre, Long idExcluir) {
        if (tipoHabitacionOut.existePorNombre(nombre, idExcluir)) {
            throw new ReglaNegocioException("Ya existe un tipo de habitación con ese nombre");
        }
    }
}
