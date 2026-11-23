package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.application.ports.out.PasswordOut;
import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService implements UsuarioIn {

    private final UsuarioOut usuarioOut;
    private final PasswordOut passwordOut;

    public UsuarioService(UsuarioOut usuarioOut, PasswordOut passwordOut) {
        this.usuarioOut = usuarioOut;
        this.passwordOut = passwordOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return this.usuarioOut.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return this.usuarioOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no existe"));
    }

    @Override
    public Usuario crear(Usuario usuario) {
        validarUsernameUnico(usuario.getUsername(), null);
        // El password se guarda encriptado (BCrypt)
        Usuario nuevo = Usuario.nuevo(usuario.getUsername(),
                this.passwordOut.encriptar(usuario.getPassword()), usuario.getRol());
        return this.usuarioOut.guardar(nuevo);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario actual = buscarPorId(id);
        validarUsernameUnico(usuario.getUsername(), id);
        actual.actualizar(usuario.getUsername(),
                this.passwordOut.encriptar(usuario.getPassword()), usuario.getRol());
        return this.usuarioOut.guardar(actual);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id);
        this.usuarioOut.eliminar(id);
    }

    @Override
    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario actual = buscarPorId(id);
        actual.cambiarEstado(activo);
        return this.usuarioOut.guardar(actual);
    }

    private void validarUsernameUnico(String username, Long idExcluir) {
        if (this.usuarioOut.existePorUsername(username, idExcluir)) {
            throw new ReglaNegocioException("El username ya existe");
        }
    }
}
