package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.HabitacionIn;
import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.HabitacionRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.HabitacionResponse;
import com.example.backend_reservashotel.infrastructure.mappers.HabitacionMapper;
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
@RequestMapping("/api/v1/habitacion")
public class HabitacionController {
    private final HabitacionIn habitacionIn;

    public HabitacionController(HabitacionIn habitacionIn) {
        this.habitacionIn = habitacionIn;
    }

    @GetMapping
    public ResponseEntity<ActionResponse<List<HabitacionResponse>>> listar() {
        List<HabitacionResponse> lista = habitacionIn.listar().stream()
                .map(HabitacionMapper::toResponse).toList();
        return ResponseEntity.ok(new ActionResponse<>("Registros encontrados: " + lista.size(), lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponse<HabitacionResponse>> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ActionResponse<>("Habitación encontrada",
                HabitacionMapper.toResponse(habitacionIn.buscarPorId(id))));
    }

    @PostMapping
    public ResponseEntity<ActionResponse<HabitacionResponse>> crear(@Valid @RequestBody HabitacionRequest request) {
        Habitacion creada = habitacionIn.crear(HabitacionMapper.toRequest(request), request.tipoHabitacionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ActionResponse<>("Habitación creada correctamente",
                HabitacionMapper.toResponse(creada)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionResponse<HabitacionResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody HabitacionRequest request) {
        Habitacion actualizada = habitacionIn.actualizar(id, HabitacionMapper.toRequest(request), request.tipoHabitacionId());
        return ResponseEntity.ok(new ActionResponse<>("Habitación actualizada correctamente",
                HabitacionMapper.toResponse(actualizada)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionResponse<Void>> eliminar(@PathVariable Long id) {
        habitacionIn.eliminar(id);
        return ResponseEntity.ok(new ActionResponse<>("Habitación eliminada correctamente", null));
    }
}
