package com.example.backend_reservashotel.application.ports.in;

import com.example.backend_reservashotel.domain.models.Usuario;

import java.util.List;

public interface UsuarioIn {
    List<Usuario> listar();

    Usuario buscarPorId(Long id);

    // Para armar la respuesta del login
    Usuario buscarPorUsername(String username);

    // El usuario llega con el password en texto plano; el servicio lo encripta
    Usuario crear(Usuario usuario);

    // Si el password viene vacío se conserva el actual
    Usuario actualizar(Long id, Usuario usuario);

    void eliminar(Long id);

    // Activar o desactivar el usuario
    Usuario cambiarEstado(Long id, boolean activo);
}
