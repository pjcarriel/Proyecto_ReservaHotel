package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.in.HabitacionIn;
import com.example.backend_reservashotel.application.ports.out.HabitacionOut;
import com.example.backend_reservashotel.application.ports.out.TipoHabitacionOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HabitacionService implements HabitacionIn {
    private final HabitacionOut habitacionOut;
    private final TipoHabitacionOut tipoHabitacionOut;

    public HabitacionService(HabitacionOut habitacionOut, TipoHabitacionOut tipoHabitacionOut) {
        this.habitacionOut = habitacionOut;
        this.tipoHabitacionOut = tipoHabitacionOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Habitacion> listar() {
        return habitacionOut.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public Habitacion buscarPorId(Long id) {
        return habitacionOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la habitación"));
    }

    @Override
    public Habitacion crear(Habitacion habitacion, Long tipoHabitacionId) {
        TipoHabitacion tipo = buscarTipo(tipoHabitacionId);
        Habitacion nueva = Habitacion.nuevo(habitacion.getNumero(), habitacion.getPiso(),
                habitacion.getPrecioNoche(), habitacion.getEstado(), tipo);
        validarNumeroUnico(nueva.getNumero(), null);
        return habitacionOut.guardar(nueva);
    }

    @Override
    public Habitacion actualizar(Long id, Habitacion habitacion, Long tipoHabitacionId) {
        Habitacion actual = buscarPorId(id);
        TipoHabitacion tipo = buscarTipo(tipoHabitacionId);
        Habitacion datos = Habitacion.nuevo(habitacion.getNumero(), habitacion.getPiso(),
                habitacion.getPrecioNoche(), habitacion.getEstado(), tipo);
        validarNumeroUnico(datos.getNumero(), id);
        actual.actualizar(datos.getNumero(), datos.getPiso(), datos.getPrecioNoche(), datos.getEstado(), tipo);
        return habitacionOut.guardar(actual);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id);
        habitacionOut.eliminar(id);
    }

    private TipoHabitacion buscarTipo(Long id) {
        return tipoHabitacionOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de habitación"));
    }

    private void validarNumeroUnico(String numero, Long idExcluir) {
        if (habitacionOut.existePorNumero(numero, idExcluir)) {
            throw new ReglaNegocioException("Ya existe una habitación con ese número");
        }
    }
}
