package com.example.backend_reservashotel.infrastructure.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.LoginRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.LoginResponse;
import com.example.backend_reservashotel.infrastructure.securities.JWTServices;

import jakarta.validation.Valid;

@RestController
@RequestMapping ("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTServices jwtServices;


    public  AuthController(AuthenticationManager authenticationManager, JWTServices jwtServices){
        this.authenticationManager= authenticationManager;
        this.jwtServices=jwtServices;
    }

    @PostMapping ("/login")
    public ResponseEntity<ActionResponse<LoginResponse>> login(@Valid @RequestBody  LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String rol = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_","");
        String token = jwtServices.generar(authentication.getName(),rol);

        return ResponseEntity.ok(new ActionResponse<>("Sesión iniciada",new LoginResponse(authentication.getName(), rol, token)));
    }

}
