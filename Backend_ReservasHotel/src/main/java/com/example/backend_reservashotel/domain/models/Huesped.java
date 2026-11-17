package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;

public class Huesped {

    private Long id;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;

    public Huesped() {
    }

    public Huesped(Long id, String cedula, String nombres, String apellidos, String telefono, String correo) {
        this.id = id;
        setCedula(cedula);
        setNombres(nombres);
        setApellidos(apellidos);
        setTelefono(telefono);
        setCorreo(correo);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new DatoInvalidoException("La cédula es obligatoria");
        }
        this.cedula = cedula.trim();
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new DatoInvalidoException("Los nombres son obligatorios");
        }
        this.nombres = nombres.trim();
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new DatoInvalidoException("Los apellidos son obligatorios");
        }
        this.apellidos = apellidos.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = (telefono != null) ? telefono.trim() : null;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = (correo != null) ? correo.trim() : null;
    }
}