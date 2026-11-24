package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.application.ports.in.ReservaIn;
import com.example.backend_reservashotel.domain.models.Reserva;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ReservaRequest;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ReservaResponse;
import com.example.backend_reservashotel.infrastructure.mappers.ReservaMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reserva")
public class ReservaController {

    private final ReservaIn reservaIn;

    public ReservaController(ReservaIn reservaIn) {
        this.reservaIn = reservaIn;
    }

    @GetMapping
    public ResponseEntity<ActionResponse<List<ReservaResponse>>> listar(
            @RequestParam(required = false) Long huespedId) {
        List<ReservaResponse> lista = reservaIn.listarPorHuesped(huespedId).stream()
                .map(ReservaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new ActionResponse<>(
                "Registros encontrados: ".concat(String.valueOf(lista.size())),
                lista
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionResponse<ReservaResponse>> buscar(@PathVariable Long id) {
        Reserva reserva = reservaIn.buscarPorId(id);
        return ResponseEntity.ok(new ActionResponse<>(
                "Reserva encontrada",
                ReservaMapper.toResponse(reserva)
        ));
    }

    @PostMapping
    public ResponseEntity<ActionResponse<ReservaResponse>> crear(@Valid @RequestBody ReservaRequest request) {
        Reserva creada = reservaIn.crear(
                request.huespedId(),
                request.fechaEntrada(),
                request.fechaSalida(),
                request.habitacionIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ActionResponse<>(
                        "Reserva registrada correctamente",
                        ReservaMapper.toResponse(creada)
                ));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ActionResponse<ReservaResponse>> cancelar(@PathVariable Long id) {
        Reserva cancelada = reservaIn.cancelar(id);
        return ResponseEntity.ok(new ActionResponse<>(
                "Reserva cancelada correctamente",
                ReservaMapper.toResponse(cancelada)
        ));
    }
}