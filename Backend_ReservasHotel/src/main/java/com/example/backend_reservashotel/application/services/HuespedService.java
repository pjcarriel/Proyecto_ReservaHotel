package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.in.HuespedIn;
import com.example.backend_reservashotel.application.ports.out.HuespedOut;
import com.example.backend_reservashotel.application.ports.out.ReservaOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Huesped;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HuespedService implements HuespedIn {

    private final HuespedOut huespedOut;
    private final ReservaOut reservaOut;

    public HuespedService(HuespedOut huespedOut, ReservaOut reservaOut) {
        this.huespedOut = huespedOut;
        this.reservaOut = reservaOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Huesped> listar() {
        return huespedOut.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public Huesped buscarPorId(Long id) {
        return huespedOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Huésped no existe"));
    }

    @Override
    public Huesped crear(Huesped huesped) {
        validarCedulaUnica(huesped.getCedula(), null);
        validarEmailUnico(huesped.getEmail(), null);
        Huesped nuevo = Huesped.nuevo(
                huesped.getCedula(),
                huesped.getNombres(),
                huesped.getApellidos(),
                huesped.getEmail(),
                huesped.getTelefono()
        );
        return huespedOut.guardar(nuevo);
    }

    @Override
    public Huesped actualizar(Long id, Huesped huesped) {
        Huesped actual = buscarPorId(id);
        validarCedulaUnica(huesped.getCedula(), id);
        validarEmailUnico(huesped.getEmail(), id);
        actual.actualizar(
                huesped.getCedula(),
                huesped.getNombres(),
                huesped.getApellidos(),
                huesped.getEmail(),
                huesped.getTelefono()
        );
        return huespedOut.guardar(actual);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id);
        if (reservaOut.existePorHuesped(id)) {
            throw new ReglaNegocioException("El huésped tiene reservas registradas");
        }
        huespedOut.eliminar(id);
    }

    private void validarCedulaUnica(String cedula, Long idExcluir) {
        if (huespedOut.existePorCedula(cedula, idExcluir)) {
            throw new ReglaNegocioException("La cédula ya existe");
        }
    }

    private void validarEmailUnico(String email, Long idExcluir) {
        if (email != null && huespedOut.existePorEmail(email, idExcluir)) {
            throw new ReglaNegocioException("El email ya existe");
        }
    }
}