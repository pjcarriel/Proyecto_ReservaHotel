package com.example.backend_reservashotel.domain.exception;

public class DatoInvalidoException extends RuntimeException {
    public DatoInvalidoException(String message) {
        super(message);
    }
}
