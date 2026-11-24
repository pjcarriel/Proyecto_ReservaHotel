package com.example.backend_reservashotel.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;

@Configuration
public class UsuarioInicial {

    private static final Logger logs = LoggerFactory.getLogger(UsuarioInicial.class);

    @Bean
    public CommandLineRunner crearAdmin(UsuarioOut usuarioOut, UsuarioIn usuarioIn) {
        return parm -> {
            if (!usuarioOut.existeAlguno()){
                // UsuarioService encripta el password con BCrypt antes de guardarlo
                usuarioIn.crear(Usuario.nuevo("admin", "Admin2026*", Rol.ADMIN));
                logs.info("Usuario admin creado con exito");
            }
        };
    }
}
