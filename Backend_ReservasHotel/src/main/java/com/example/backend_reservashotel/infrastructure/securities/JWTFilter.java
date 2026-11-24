package com.example.backend_reservashotel.infrastructure.securities;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class JWTFilter extends OncePerRequestFilter{
    private final JWTServices jwtServices;
    public JWTFilter(JWTServices jwtServices){
        this.jwtServices=jwtServices;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);

            try{
                Claims payLoad = jwtServices.leer(token);
                String rol = payLoad.get("rol", String.class);
                UsernamePasswordAuthenticationToken autenticado = new UsernamePasswordAuthenticationToken(
                    payLoad.getSubject(), null,
                    List.of(new SimpleGrantedAuthority("ROLE_".concat(rol))));
                SecurityContextHolder.getContext().setAuthentication(autenticado);

            }catch (JwtException ex){
                // Token invalido o expirado: sigue sin autenticar y Security responde 401
                SecurityContextHolder.clearContext();
            }
        }
        // Siempre continuar la cadena (con o sin token), si no el login nunca llega al controlador
        filterChain.doFilter(request, response);
    }
}
