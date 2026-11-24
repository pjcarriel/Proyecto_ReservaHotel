package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.UsuarioIn;
import com.example.backend_reservashotel.domain.models.Usuario;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.UsuarioRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.UsuarioResponse;
import com.example.backend_reservashotel.infrastructure.mappers.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuario")
public class UsuarioController {

    private final UsuarioIn usuarioIn;

    public UsuarioController(UsuarioIn usuarioIn) {
        this.usuarioIn = usuarioIn;
    }

    @GetMapping
    public ResponseEntity<ActionResponse<List<UsuarioResponse>>> listar(){
        List<UsuarioResponse> lista = usuarioIn.listar().stream()
                .map(UsuarioMapper::toResponse).toList();
        return ResponseEntity.ok(new ActionResponse<>("Registros encontrados: ".concat(String.valueOf(lista.size())),
                lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponse<UsuarioResponse>> buscar(@PathVariable Long id){
        Usuario usuario = usuarioIn.buscarPorId(id);
        return ResponseEntity.ok(new ActionResponse<>("Usuario encontrado",
                UsuarioMapper.toResponse(usuario)));
    }

    @PostMapping
    public ResponseEntity<ActionResponse<UsuarioResponse>> crear(@Valid @RequestBody UsuarioRequest request){
        Usuario creado = usuarioIn.crear(UsuarioMapper.toRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ActionResponse<>("Usuario creado correctamente",
                        UsuarioMapper.toResponse(creado)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionResponse<UsuarioResponse>> actualizar(@PathVariable Long id,
                                                                     @Valid @RequestBody UsuarioRequest request){
        Usuario actualizado = usuarioIn.actualizar(id, UsuarioMapper.toRequest(request));
        return ResponseEntity.ok(new ActionResponse<>("Usuario actualizado correctamente",
                UsuarioMapper.toResponse(actualizado)));
    }

    // Activar o desactivar: body {"activo": true} o {"activo": false}
    @PatchMapping("/{id}")
    public ResponseEntity<ActionResponse<UsuarioResponse>> cambiarEstado(@PathVariable Long id,
                                                                        @RequestBody Map<String, Boolean> estado){
        Usuario modificado = usuarioIn.cambiarEstado(id, Boolean.TRUE.equals(estado.get("activo")));
        return ResponseEntity.ok(new ActionResponse<>("Estado del usuario modificado",
                UsuarioMapper.toResponse(modificado)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionResponse<Void>> eliminar(@PathVariable Long id){
        usuarioIn.eliminar(id);
        return ResponseEntity.ok(new ActionResponse<>("Usuario eliminado correctamente", null));
    }
}
