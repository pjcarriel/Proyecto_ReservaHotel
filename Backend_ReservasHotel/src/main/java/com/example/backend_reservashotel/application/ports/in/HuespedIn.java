package com.example.backend_reservashotel.application.ports.in;

import com.example.backend_reservashotel.domain.models.Huesped;
import java.util.List;

public interface HuespedIn {
    List<Huesped> listar();
    Huesped crear(Huesped huesped);
    Huesped actualizar(Long id, Huesped huesped);
    void eliminar(Long id);
    Huesped buscarPorId(Long id);
}