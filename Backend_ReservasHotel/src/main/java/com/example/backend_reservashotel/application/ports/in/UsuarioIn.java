package com.example.backend_reservashotel.application.ports.in;

import java.util.List;

import com.example.backend_reservashotel.domain.models.Usuario;

public interface UsuarioIn {
    List<Usuario> listar();
    Usuario buscarPorId(Long id);
    Usuario crear(Usuario usuario);
    Usuario actualizar(Long id, Usuario usuario);
    void eliminar(Long id);
    // Activar o desactivar el usuario
    Usuario cambiarEstado(Long id, boolean activo);
}
