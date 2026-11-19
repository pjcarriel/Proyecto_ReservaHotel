package com.example.backend_reservashotel.infrastructure.securities;

import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.models.Usuario;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security lo usa en el login para buscar al usuario y comparar el password con BCrypt
@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioOut usuarioOut;

    public UsuarioDetailsService(UsuarioOut usuarioOut) {
        this.usuarioOut = usuarioOut;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioOut.buscarPorUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .disabled(!usuario.isActivo())   // un usuario desactivado no puede iniciar sesión
                .build();
    }
}
