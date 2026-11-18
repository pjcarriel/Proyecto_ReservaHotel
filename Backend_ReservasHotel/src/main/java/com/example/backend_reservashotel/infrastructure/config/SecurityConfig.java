package com.example.backend_reservashotel.infrastructure.config;

import java.io.IOException;
import java.util.List;

import com.example.backend_reservashotel.infrastructure.securities.JWTFilter;
import com.example.backend_reservashotel.infrastructure.securities.JWTServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Orígenes permitidos (se leen de application.properties)
    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    private final JWTServices jwtServices;

    public SecurityConfig(JWTServices jwtServices) {
        this.jwtServices = jwtServices;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/health").permitAll()
                        // TODO (tarea #27, jueves 26): proteger por rol, por ejemplo
                        //   .requestMatchers("/api/v1/usuario/**").hasRole("ADMIN")
                        //   .anyRequest().authenticated()
                        // Mientras tanto todo sigue abierto para que el equipo pueda probar sin token
                        .anyRequest().permitAll()
                )
                // Respuestas en formato ActionResponse cuando falta el token o no tiene permiso
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                responder(response, HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión"))
                        .accessDeniedHandler((request, response, e) ->
                                responder(response, HttpServletResponse.SC_FORBIDDEN, "No tiene permiso para esta acción")))
                .addFilterBefore(new JWTFilter(jwtServices), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Usa UsuarioDetailsService y el PasswordEncoder (BCrypt) para validar el login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private static void responder(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + mensaje + "\",\"result\":null}");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
