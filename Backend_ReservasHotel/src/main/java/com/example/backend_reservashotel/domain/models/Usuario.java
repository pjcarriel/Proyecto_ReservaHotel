package com.example.backend_reservashotel.domain.models;

import java.time.LocalDateTime;

// Representa el dominio del Usuario
public class Usuario {

    private Long id;
    private String username;
    private String password;
    private Rol rol;
    private boolean activo;
    private LocalDateTime creado;
    private LocalDateTime modificado;

    // Constructor
    public Usuario(Long id, String username, String password, Rol rol, boolean activo,
                   LocalDateTime creado, LocalDateTime modificado) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
        this.creado = creado;
        this.modificado = modificado;
    }

    // Valores que son propios de un ingreso de usuario
    // Un usuario nuevo siempre inicia activo
    public static Usuario nuevo(String username, String password, Rol rol) {
        return new Usuario(null, username, password, rol, true, null, null);
    }

    // Para actualizar, utilizar el objeto que ya fue convertido al dominio
    // SOLO CAMPOS QUE SON INGRESADOS POR EL USUARIO
    public void actualizar(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    // Activar o desactivar el acceso del usuario
    public void cambiarEstado(boolean activo) {
        this.activo = activo;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getCreado() {
        return creado;
    }

    public LocalDateTime getModificado() {
        return modificado;
    }
}
