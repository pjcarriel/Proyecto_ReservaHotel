package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Endpoint simple para probar que Angular se conecta al backend
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ActionResponse<String>> health() {
        return ResponseEntity.ok(new ActionResponse<>("Backend funcionando", "UP"));
    }
}
