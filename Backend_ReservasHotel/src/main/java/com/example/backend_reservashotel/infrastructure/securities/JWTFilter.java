package com.example.backend_reservashotel.infrastructure.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Lee el header "Authorization: Bearer <token>" y deja al usuario autenticado con su rol.
// No se registra como @Component para que no se ejecute dos veces; lo agrega SecurityConfig.
public class JWTFilter extends OncePerRequestFilter {
    private static final String PREFIJO = "Bearer ";

    private final JWTServices jwtServices;

    public JWTFilter(JWTServices jwtServices) {
        this.jwtServices = jwtServices;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIJO)) {
            try {
                Claims datos = jwtServices.leer(header.substring(PREFIJO.length()));
                String rol = datos.get(JWTServices.CLAIM_ROL, String.class);
                if (datos.getSubject() != null && rol != null) {
                    UsernamePasswordAuthenticationToken autenticado = new UsernamePasswordAuthenticationToken(
                            datos.getSubject(), null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                    SecurityContextHolder.getContext().setAuthentication(autenticado);
                }
            } catch (JwtException | IllegalArgumentException ex) {
                // Token inválido o expirado: la petición sigue sin autenticar
                SecurityContextHolder.clearContext();
            }
        }
        // Siempre continuar la cadena (con o sin token); si no, el login nunca llega al controlador
        filterChain.doFilter(request, response);
    }
}
