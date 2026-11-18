package com.example.backend_reservashotel.domain.models;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;

import java.time.LocalDateTime;

// Representa el dominio del Usuario.
// El password que guarda el dominio siempre es el hash (BCrypt), nunca el texto plano.
public class Usuario {
    private Long id;
    private String username;
    private String nombre;
    private String password;
    private Rol rol;
    private boolean activo;
    private LocalDateTime creado;
    private LocalDateTime modificado;

    // Constructor completo para reconstruir el dominio desde la persistencia
    public Usuario(Long id, String username, String nombre, String password, Rol rol, boolean activo,
                   LocalDateTime creado, LocalDateTime modificado) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
        this.creado = creado;
        this.modificado = modificado;
    }

    // Un usuario nuevo siempre inicia activo. El password se encripta en el servicio.
    public static Usuario nuevo(String username, String nombre, String password, Rol rol) {
        validarDatosBasicos(username, nombre, rol);
        return new Usuario(null, username.trim(), nombre.trim(), password, rol, true, null, null);
    }

    // Actualiza solo los campos ingresados por el usuario (el password se cambia aparte)
    public void actualizar(String username, String nombre, Rol rol) {
        validarDatosBasicos(username, nombre, rol);
        this.username = username.trim();
        this.nombre = nombre.trim();
        this.rol = rol;
    }

    // Recibe el password ya encriptado
    public void cambiarPassword(String passwordEncriptado) {
        if (passwordEncriptado == null || passwordEncriptado.isBlank()) {
            throw new DatoInvalidoException("El password es obligatorio");
        }
        this.password = passwordEncriptado;
    }

    // Activar o desactivar el acceso del usuario
    public void cambiarEstado(boolean activo) {
        this.activo = activo;
    }

    public boolean esAdminActivo() {
        return activo && rol == Rol.ADMIN;
    }

    private static void validarDatosBasicos(String username, String nombre, Rol rol) {
        if (username == null || username.trim().isEmpty()) {
            throw new DatoInvalidoException("El username no puede estar vacío");
        }
        if (username.trim().contains(" ")) {
            throw new DatoInvalidoException("El username no puede tener espacios");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre no puede estar vacío");
        }
        if (rol == null) {
            throw new DatoInvalidoException("El rol es obligatorio");
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNombre() {
        return nombre;
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
