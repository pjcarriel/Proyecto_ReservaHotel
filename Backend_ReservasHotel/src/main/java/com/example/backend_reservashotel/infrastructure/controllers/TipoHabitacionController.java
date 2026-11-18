package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.TipoHabitacionIn;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.TipoHabitacionRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.TipoHabitacionResponse;
import com.example.backend_reservashotel.infrastructure.mappers.TipoHabitacionMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipo-habitacion")
public class TipoHabitacionController {
    private final TipoHabitacionIn tipoHabitacionIn;

    public TipoHabitacionController(TipoHabitacionIn tipoHabitacionIn) {
        this.tipoHabitacionIn = tipoHabitacionIn;
    }

    @GetMapping
    public ResponseEntity<ActionResponse<List<TipoHabitacionResponse>>> listar() {
        List<TipoHabitacionResponse> lista = tipoHabitacionIn.listar().stream()
                .map(TipoHabitacionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new ActionResponse<>("Registros encontrados: " + lista.size(), lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponse<TipoHabitacionResponse>> buscar(@PathVariable Long id) {
        TipoHabitacion tipoHabitacion = tipoHabitacionIn.buscarPorId(id);
        return ResponseEntity.ok(new ActionResponse<>("Tipo de habitación encontrado",
                TipoHabitacionMapper.toResponse(tipoHabitacion)));
    }

    @PostMapping
    public ResponseEntity<ActionResponse<TipoHabitacionResponse>> crear(
            @Valid @RequestBody TipoHabitacionRequest request) {
        TipoHabitacion creado = tipoHabitacionIn.crear(TipoHabitacionMapper.toRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ActionResponse<>("Tipo de habitación creado correctamente",
                        TipoHabitacionMapper.toResponse(creado)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionResponse<TipoHabitacionResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody TipoHabitacionRequest request) {
        TipoHabitacion actualizado = tipoHabitacionIn.actualizar(id, TipoHabitacionMapper.toRequest(request));
        return ResponseEntity.ok(new ActionResponse<>("Tipo de habitación actualizado correctamente",
                TipoHabitacionMapper.toResponse(actualizado)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionResponse<Void>> eliminar(@PathVariable Long id) {
        tipoHabitacionIn.eliminar(id);
        return ResponseEntity.ok(new ActionResponse<>("Tipo de habitación eliminado correctamente", null));
    }
}
