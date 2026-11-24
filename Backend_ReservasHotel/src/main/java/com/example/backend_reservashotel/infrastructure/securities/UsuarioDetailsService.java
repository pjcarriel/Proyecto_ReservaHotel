package com.example.backend_reservashotel.infrastructure.securities;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.models.Usuario;

@Service
public class UsuarioDetailsService implements UserDetailsService{

    private final UsuarioOut usuarioOut;

    public UsuarioDetailsService (UsuarioOut usuarioOut){
        this.usuarioOut = usuarioOut;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Usuario usuario = usuarioOut.buscarPorUsername(username).
            orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .disabled(!usuario.isActivo())
                .build();
    }
}
