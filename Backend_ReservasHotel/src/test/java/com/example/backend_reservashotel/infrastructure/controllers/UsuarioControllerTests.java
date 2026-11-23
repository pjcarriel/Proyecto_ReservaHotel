package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.domain.models.Rol;
import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.config.SecurityConfig;
import com.example.backend_reservashotel.infrastructure.securities.JWTServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfig.class, JWTServices.class})
@TestPropertySource(properties = {
        "app.cors.allowed-origins=http://localhost:4200",
        "jwt.secret=8hwE1MqQ7W2sRFAqqDOJo+p2QJCJTXwxtoIex32R8bA=",
        "jwt.expiration=60000"
})
class UsuarioControllerTests {
    private static final String URL = "/api/v1/usuario";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioIn usuarioIn;

    @Test
    void listarNoDevuelveElPassword() throws Exception {
        when(usuarioIn.listar()).thenReturn(List.of(admin(true)));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registros encontrados: 1"))
                .andExpect(jsonPath("$.result[0].username").value("admin"))
                .andExpect(jsonPath("$.result[0].rol").value("ADMIN"))
                .andExpect(jsonPath("$.result[0].password").doesNotExist());
    }

    @Test
    void crearDevuelve201() throws Exception {
        when(usuarioIn.crear(any())).thenReturn(admin(true));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"admin","password":"Admin2026*","rol":"ADMIN"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuario creado correctamente"))
                .andExpect(jsonPath("$.result.password").doesNotExist());
    }

    @Test
    void crearConDatosInvalidosDevuelve400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"ab","password":"corta"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.username").exists())
                .andExpect(jsonPath("$.result.password").exists())
                .andExpect(jsonPath("$.result.rol").exists());

        verify(usuarioIn, never()).crear(any());
    }

    @Test
    void patchDesactivaElUsuario() throws Exception {
        when(usuarioIn.cambiarEstado(1L, false)).thenReturn(admin(false));

        mockMvc.perform(patch(URL + "/1").contentType(MediaType.APPLICATION_JSON).content("""
                        {"activo": false}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Estado del usuario modificado"))
                .andExpect(jsonPath("$.result.activo").value(false));
    }

    @Test
    void usernameRepetidoDevuelve409() throws Exception {
        when(usuarioIn.crear(any())).thenThrow(new ReglaNegocioException("El username ya existe"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"username":"admin","password":"Admin2026*","rol":"ADMIN"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El username ya existe"));
    }

    private static Usuario admin(boolean activo) {
        return new Usuario(1L, "admin", "$2a$10$hash", Rol.ADMIN, activo,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
