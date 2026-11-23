package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.infrastructure.config.SecurityConfig;
import com.example.backend_reservashotel.infrastructure.securities.JWTServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

    @Test
    void loginCorrectoDevuelveTokenConElRol() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(UsernamePasswordAuthenticationToken
                .authenticated("admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        String json = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"admin","password":"Admin2026*"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sesión iniciada"))
                .andExpect(jsonPath("$.result.username").value("admin"))
                .andExpect(jsonPath("$.result.rol").value("ADMIN"))
                .andReturn().getResponse().getContentAsString();

        String token = json.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        assertEquals("admin", jwtServices.leer(token).getSubject());
        assertEquals("ADMIN", jwtServices.leer(token).get("rol", String.class));
    }

    @Test
    void passwordIncorrectoDevuelve401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"admin","password":"incorrecta"}
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginSinDatosDevuelve400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"","password":""}
                        """))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }
}
