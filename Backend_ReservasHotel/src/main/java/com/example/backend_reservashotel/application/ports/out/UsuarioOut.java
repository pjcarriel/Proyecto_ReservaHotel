package com.example.backend_reservashotel.application.ports.out;

import java.util.List;
import java.util.Optional;

import com.example.backend_reservashotel.domain.models.Usuario;

public interface UsuarioOut {
    List<Usuario> listar();
    Optional<Usuario> buscarPorId(Long id);
    Usuario guardar(Usuario usuario);
    void eliminar(Long id);

    // Username unico (id para excluir al mismo usuario al actualizar)
    boolean existePorUsername(String username, Long id);
    Optional<Usuario> buscarPorUsername(String username);
    boolean existeAlguno();
}
