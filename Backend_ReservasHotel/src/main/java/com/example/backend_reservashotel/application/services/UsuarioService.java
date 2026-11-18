package com.example.backend_reservashotel.application.services;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.application.ports.out.PasswordOut;
import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService implements UsuarioIn {
    // BCrypt solo usa los primeros 72 bytes del password
    private static final int PASSWORD_MIN = 8;
    private static final int PASSWORD_MAX = 72;

    private final UsuarioOut usuarioOut;
    private final PasswordOut passwordOut;

    public UsuarioService(UsuarioOut usuarioOut, PasswordOut passwordOut) {
        this.usuarioOut = usuarioOut;
        this.passwordOut = passwordOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioOut.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioOut.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario"));
    }

    @Override
    public Usuario crear(Usuario usuario) {
        String passwordPlano = usuario.getPassword();
        if (passwordPlano == null || passwordPlano.isBlank()) {
            throw new DatoInvalidoException("El password es obligatorio");
        }
        validarPassword(passwordPlano);

        Usuario nuevo = Usuario.nuevo(usuario.getUsername(), usuario.getNombre(), null, usuario.getRol());
        validarUsernameUnico(nuevo.getUsername(), null);
        nuevo.cambiarPassword(passwordOut.encriptar(passwordPlano));
        return usuarioOut.guardar(nuevo);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario actual = buscarPorId(id);
        validarUsernameUnico(usuario.getUsername().trim(), id);

        // Si se le quita el rol ADMIN al último administrador, nadie podría gestionar usuarios
        if (actual.esAdminActivo() && usuario.getRol() != Rol.ADMIN) {
            validarQuedaOtroAdmin();
        }
        actual.actualizar(usuario.getUsername(), usuario.getNombre(), usuario.getRol());

        // El password es opcional al actualizar: si viene vacío se conserva el actual
        String passwordPlano = usuario.getPassword();
        if (passwordPlano != null && !passwordPlano.isBlank()) {
            validarPassword(passwordPlano);
            actual.cambiarPassword(passwordOut.encriptar(passwordPlano));
        }
        return usuarioOut.guardar(actual);
    }

    @Override
    public void eliminar(Long id) {
        Usuario actual = buscarPorId(id);
        if (actual.esAdminActivo()) {
            validarQuedaOtroAdmin();
        }
        usuarioOut.eliminar(id);
    }

    @Override
    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario actual = buscarPorId(id);
        if (!activo && actual.esAdminActivo()) {
            validarQuedaOtroAdmin();
        }
        actual.cambiarEstado(activo);
        return usuarioOut.guardar(actual);
    }

    private void validarUsernameUnico(String username, Long idExcluir) {
        if (usuarioOut.existePorUsername(username, idExcluir)) {
            throw new ReglaNegocioException("Ya existe un usuario con ese username");
        }
    }

    private void validarPassword(String passwordPlano) {
        if (passwordPlano.length() < PASSWORD_MIN || passwordPlano.length() > PASSWORD_MAX) {
            throw new DatoInvalidoException("El password debe tener entre "
                    + PASSWORD_MIN + " y " + PASSWORD_MAX + " caracteres");
        }
    }

    private void validarQuedaOtroAdmin() {
        if (usuarioOut.contarAdminsActivos() <= 1) {
            throw new ReglaNegocioException("Debe quedar al menos un administrador activo");
        }
    }
}
