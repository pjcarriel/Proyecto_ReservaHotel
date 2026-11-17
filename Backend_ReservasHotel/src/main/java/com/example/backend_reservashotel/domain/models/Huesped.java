package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import java.time.LocalDateTime;

// Representa el dominio del Huesped
public class Huesped {
    private Long id;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private LocalDateTime creado;
    private LocalDateTime modificado;

    // Constructor completo para mapear desde la base de datos (Persistencia)
    public Huesped(Long id, String cedula, String nombres, String apellidos, String email, String telefono,
                   LocalDateTime creado, LocalDateTime modificado) {
        this.id = id;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.creado = creado;
        this.modificado = modificado;
    }

    // Valores que son propios de un ingreso de usuario
    // SOLO CAMPOS QUE SON INGRESADOS POR EL USUARIO
    public static Huesped nuevo(String cedula, String nombres, String apellidos, String email, String telefono) {
        validarDatosBasicos(cedula, nombres, apellidos);
        return new Huesped(null, cedula, nombres, apellidos, email, telefono, null, null);
    }

    // Para actualizar, utilizar el objeto que ya fue convertido al dominio
    // SOLO CAMPOS QUE SON INGRESADOS POR EL USUARIO
    public void actualizar(String cedula, String nombres, String apellidos, String email, String telefono) {
        validarDatosBasicos(cedula, nombres, apellidos);
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
    }

    // Metodo de validacion basico del dominio
    private static void validarDatosBasicos(String cedula, String nombres, String apellidos) {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new DatoInvalidoException("La cédula no puede estar vacía");
        }
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new DatoInvalidoException("Los nombres no pueden estar vacíos");
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new DatoInvalidoException("Los apellidos no pueden estar vacíos");
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDateTime getCreado() {
        return creado;
    }

    public LocalDateTime getModificado() {
        return modificado;
    }
}