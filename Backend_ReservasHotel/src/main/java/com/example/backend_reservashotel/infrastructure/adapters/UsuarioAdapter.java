package com.example.backend_reservashotel.infrastructure.adapters;

import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.entities.UsuarioEntity;
import com.example.backend_reservashotel.infrastructure.mappers.UsuarioMapper;
import com.example.backend_reservashotel.infrastructure.repositories.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
// Implementa el puerto de salida usando JPA
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
        // Sin id: entity nueva, con id: se actualiza la entity existente
        UsuarioEntity usuEntity = usuario.getId() == null
                ? new UsuarioEntity()
                : usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        UsuarioMapper.toEntity(usuario, usuEntity);
        return UsuarioMapper.toDomain(usuarioRepository.saveAndFlush(usuEntity));
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
        usuarioRepository.flush();
    }

    @Override
    public boolean existePorUsername(String username, Long id) {
        // Sin id: al crear, con id: al actualizar (excluye al mismo usuario)
        return id == null
                ? usuarioRepository.existsByUsernameIgnoreCase(username)
                : usuarioRepository.existsByUsernameIgnoreCaseAndIdNot(username, id);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).map(UsuarioMapper::toDomain);
    }

    @Override
    public boolean existeAlguno() {
        return usuarioRepository.count() > 0;
    }
}
