package com.example.backend_reservashotel.domain.models;

import java.time.LocalDateTime;

public class TipoHabitacion {
    private Long id;
    private String nombre;
    private String descripcion;
    private int capacidad;
    private LocalDateTime creado;
    private LocalDateTime modificado;

    public TipoHabitacion(Long id, String nombre, String descripcion, int capacidad,
                          LocalDateTime creado, LocalDateTime modificado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
        this.creado = creado;
        this.modificado = modificado;
    }

    // El id y las fechas de auditoria se asignan al persistir, como en Categoria.
    public static TipoHabitacion nuevo(String nombre, String descripcion, int capacidad) {
        return new TipoHabitacion(null, nombre, descripcion, capacidad, null, null);
    }

    // Actualiza solo los campos ingresados por el usuario.
    public void actualizar(String nombre, String descripcion, int capacidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public LocalDateTime getCreado() {
        return creado;
    }

    public LocalDateTime getModificado() {
        return modificado;
    }
}
