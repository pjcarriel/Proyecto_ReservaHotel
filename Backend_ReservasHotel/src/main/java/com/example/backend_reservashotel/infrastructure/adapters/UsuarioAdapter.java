package com.example.backend_reservashotel.infrastructure.adapters;

import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.entities.UsuarioEntity;
import com.example.backend_reservashotel.infrastructure.mappers.UsuarioMapper;
import com.example.backend_reservashotel.infrastructure.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioAdapter implements UsuarioOut {
    private final UsuarioRepository usuarioRepository;

    public UsuarioAdapter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(UsuarioMapper::toDomain);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // Sin id: entity nueva; con id: se actualiza la entity existente
        UsuarioEntity entity = usuario.getId() == null
                ? new UsuarioEntity()
                : usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario"));
        UsuarioMapper.toEntity(usuario, entity);
        try {
            return UsuarioMapper.toDomain(usuarioRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            // La restricción UNIQUE también protege frente a dos creaciones simultáneas
            throw new ReglaNegocioException("No se pudo guardar el usuario; verifica que el username sea único");
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            usuarioRepository.deleteById(id);
            usuarioRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("No se puede eliminar el usuario porque tiene registros asociados; desactívalo");
        }
    }

    @Override
    public boolean existePorUsername(String username, Long idExcluir) {
        return idExcluir == null
                ? usuarioRepository.existsByUsernameIgnoreCase(username)
                : usuarioRepository.existsByUsernameIgnoreCaseAndIdNot(username, idExcluir);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsernameIgnoreCase(username).map(UsuarioMapper::toDomain);
    }

    @Override
    public boolean existeAlguno() {
        return usuarioRepository.count() > 0;
    }

    @Override
    public long contarAdminsActivos() {
        return usuarioRepository.countByRolAndActivoTrue(Rol.ADMIN);
    }
}
