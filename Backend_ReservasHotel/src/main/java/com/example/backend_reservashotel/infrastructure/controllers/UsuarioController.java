package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.EstadoUsuarioRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.UsuarioRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.UsuarioResponse;
import com.example.backend_reservashotel.infrastructure.mappers.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// TODO (tarea #27): solo el rol ADMIN podrá usar estas rutas
@RestController
@RequestMapping("/api/v1/usuario")
public class UsuarioController {
    private final UsuarioIn usuarioIn;

    public UsuarioController(UsuarioIn usuarioIn) {
        this.usuarioIn = usuarioIn;
    }

    @GetMapping
    public ResponseEntity<ActionResponse<List<UsuarioResponse>>> listar() {
        List<UsuarioResponse> lista = usuarioIn.listar().stream()
                .map(UsuarioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new ActionResponse<>("Registros encontrados: " + lista.size(), lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponse<UsuarioResponse>> buscar(@PathVariable Long id) {
        Usuario usuario = usuarioIn.buscarPorId(id);
        return ResponseEntity.ok(new ActionResponse<>("Usuario encontrado", UsuarioMapper.toResponse(usuario)));
    }

    @PostMapping
    public ResponseEntity<ActionResponse<UsuarioResponse>> crear(@Valid @RequestBody UsuarioRequest request) {
        Usuario creado = usuarioIn.crear(UsuarioMapper.toRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ActionResponse<>("Usuario creado correctamente", UsuarioMapper.toResponse(creado)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionResponse<UsuarioResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        Usuario actualizado = usuarioIn.actualizar(id, UsuarioMapper.toRequest(request));
        return ResponseEntity.ok(new ActionResponse<>("Usuario actualizado correctamente",
                UsuarioMapper.toResponse(actualizado)));
    }

    // Activar o desactivar: body {"activo": true} o {"activo": false}
    @PatchMapping("/{id}")
    public ResponseEntity<ActionResponse<UsuarioResponse>> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody EstadoUsuarioRequest request) {
        Usuario modificado = usuarioIn.cambiarEstado(id, request.activo());
        String mensaje = modificado.isActivo() ? "Usuario activado" : "Usuario desactivado";
        return ResponseEntity.ok(new ActionResponse<>(mensaje, UsuarioMapper.toResponse(modificado)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioIn.eliminar(id);
        return ResponseEntity.ok(new ActionResponse<>("Usuario eliminado correctamente", null));
    }
}
