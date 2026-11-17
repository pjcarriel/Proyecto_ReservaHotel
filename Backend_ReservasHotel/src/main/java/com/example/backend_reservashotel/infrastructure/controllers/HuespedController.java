package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.HuespedIn;
import com.example.backend_reservashotel.domain.models.Huesped;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.HuespedRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.HuespedResponse;
import com.example.backend_reservashotel.infrastructure.mappers.HuespedMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/huesped")
public class HuespedController {

    private final HuespedIn huespedIn;

    public HuespedController(HuespedIn huespedIn) {
        this.huespedIn = huespedIn;
    }

    @GetMapping
    public ResponseEntity<ActionResponse<List<HuespedResponse>>> listar() {
        List<HuespedResponse> lista = huespedIn.listar().stream()
                .map(HuespedMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new ActionResponse<>(
                "Registros encontrados: ".concat(String.valueOf(lista.size())),
                lista
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponse<HuespedResponse>> buscar(@PathVariable Long id) {
        Huesped huesped = huespedIn.buscarPorId(id);
        return ResponseEntity.ok(new ActionResponse<>(
                "Huésped encontrado",
                HuespedMapper.toResponse(huesped)
        ));
    }

    @PostMapping
    public ResponseEntity<ActionResponse<HuespedResponse>> crear(@Valid @RequestBody HuespedRequest request) {
        Huesped creado = huespedIn.crear(HuespedMapper.toRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ActionResponse<>(
                        "Huésped creado correctamente",
                        HuespedMapper.toResponse(creado)
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionResponse<HuespedResponse>> actualizar(@PathVariable Long id,
                                                                     @Valid @RequestBody HuespedRequest request) {
        Huesped actualizado = huespedIn.actualizar(id, HuespedMapper.toRequest(request));
        return ResponseEntity.ok(new ActionResponse<>(
                "Huésped actualizado correctamente",
                HuespedMapper.toResponse(actualizado)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionResponse<Void>> eliminar(@PathVariable Long id) {
        huespedIn.eliminar(id);
        return ResponseEntity.ok(new ActionResponse<>("Huésped eliminado correctamente", null));
    }
}