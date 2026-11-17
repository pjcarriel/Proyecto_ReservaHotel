package com.example.backend_reservashotel.application.ports.out;

import com.example.backend_reservashotel.domain.models.Huesped;
import java.util.List;
import java.util.Optional;

public interface HuespedOut {
    List<Huesped> listar();
    Huesped guardar(Huesped huesped);
    void eliminar(Long id);
    Optional<Huesped> buscarPorId(Long id);
    boolean existePorId(Long id);
    // Cedula y email unicos (id para excluir al mismo huesped al actualizar)
    boolean existePorCedula(String cedula, Long id);
    boolean existePorEmail(String email, Long id);
}