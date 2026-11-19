package com.example.backend_reservashotel.infrastructure.config;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.application.ports.out.UsuarioOut;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Crea el usuario administrador la primera vez que arranca el sistema (tabla usuario vacía)
@Configuration
public class UsuarioInicial {
    private static final Logger log = LoggerFactory.getLogger(UsuarioInicial.class);

    @Bean
    public CommandLineRunner crearAdmin(UsuarioOut usuarioOut, UsuarioIn usuarioIn,
                                        @Value("${app.admin.username}") String username,
                                        @Value("${app.admin.password}") String password) {
        return args -> {
            if (!usuarioOut.existeAlguno()) {
                // UsuarioService encripta el password con BCrypt
                usuarioIn.crear(Usuario.nuevo(username, "Administrador", password, Rol.ADMIN));
                log.info("Usuario administrador '{}' creado. Cambia su password después del primer ingreso.", username);
            }
        };
    }
}
