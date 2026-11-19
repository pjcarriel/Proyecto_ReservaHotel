package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.config.SecurityConfig;
import com.example.backend_reservashotel.infrastructure.securities.JWTServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JWTServices.class})
@TestPropertySource(properties = {
        "app.cors.allowed-origins=http://localhost:4200",
        "jwt.secret=8hwE1MqQ7W2sRFAqqDOJo+p2QJCJTXwxtoIex32R8bA=",
        "jwt.expiration=60000"
})
class AuthControllerTests {
    private static final String URL = "/api/v1/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTServices jwtServices;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UsuarioIn usuarioIn;

    @Test
    void loginCorrectoDevuelveTokenConElRol() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(
                UsernamePasswordAuthenticationToken.authenticated("admin", null, List.of()));
        when(usuarioIn.buscarPorUsername("admin")).thenReturn(new Usuario(1L, "admin", "Administrador",
                "$2a$10$hash", Rol.ADMIN, true, LocalDateTime.now(), LocalDateTime.now()));

        String json = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":" admin ","password":"Admin2026*"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sesión iniciada"))
                .andExpect(jsonPath("$.result.token", not(emptyOrNullString())))
                .andExpect(jsonPath("$.result.username").value("admin"))
                .andExpect(jsonPath("$.result.nombre").value("Administrador"))
                .andExpect(jsonPath("$.result.rol").value("ADMIN"))
                .andExpect(jsonPath("$.result.expiraEn").value(60000))
                .andReturn().getResponse().getContentAsString();

        String token = json.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        assertEquals("admin", jwtServices.leer(token).getSubject());
        assertEquals("ADMIN", jwtServices.leer(token).get(JWTServices.CLAIM_ROL, String.class));
    }

    @Test
    void passwordIncorrectoDevuelve401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"admin","password":"incorrecta"}
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"));
    }

    @Test
    void usuarioDesactivadoDevuelve401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("User is disabled"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"recepcion1","password":"clave12345"}
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginSinDatosDevuelve400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"","password":""}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.username").exists())
                .andExpect(jsonPath("$.result.password").exists());

        verify(authenticationManager, never()).authenticate(any());
    }
}
