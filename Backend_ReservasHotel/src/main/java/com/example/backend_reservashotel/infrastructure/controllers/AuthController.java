package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.LoginRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.LoginResponse;
import com.example.backend_reservashotel.infrastructure.securities.JWTServices;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTServices jwtServices;
    private final UsuarioIn usuarioIn;

    public AuthController(AuthenticationManager authenticationManager, JWTServices jwtServices,
                          UsuarioIn usuarioIn) {
        this.authenticationManager = authenticationManager;
        this.jwtServices = jwtServices;
        this.usuarioIn = usuarioIn;
    }

    // Si el usuario o el password no coinciden, o el usuario está desactivado,
    // Spring lanza AuthenticationException y GlobalExceptionHandler responde 401
    @PostMapping("/login")
    public ResponseEntity<ActionResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Authentication autenticado = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username().trim(), request.password()));

        Usuario usuario = usuarioIn.buscarPorUsername(autenticado.getName());
        String token = jwtServices.generar(usuario.getUsername(), usuario.getRol().name());

        return ResponseEntity.ok(new ActionResponse<>("Sesión iniciada",
                new LoginResponse(token, usuario.getUsername(), usuario.getNombre(), usuario.getRol(),
                        jwtServices.getExpiracion())));
    }
}
