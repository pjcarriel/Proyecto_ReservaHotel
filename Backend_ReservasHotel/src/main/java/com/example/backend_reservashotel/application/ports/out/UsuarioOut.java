package com.example.backend_reservashotel.application.ports.out;

import com.example.backend_reservashotel.domain.models.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioOut {
    List<Usuario> listar();

    Optional<Usuario> buscarPorId(Long id);

    Usuario guardar(Usuario usuario);

    void eliminar(Long id);

    // Username único (al crear idExcluir es null; al actualizar es el id del mismo usuario)
    boolean existePorUsername(String username, Long idExcluir);

    // Para el login (tarea #11)
    Optional<Usuario> buscarPorUsername(String username);

    // Para crear el admin inicial si la tabla está vacía (tarea #11)
    boolean existeAlguno();

    // Para no dejar el sistema sin ningún administrador activo
    long contarAdminsActivos();
}
