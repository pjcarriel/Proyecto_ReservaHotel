package com.example.backend_reservashotel.infrastructure.controllers;

import com.example.backend_reservashotel.domain.exception.DatoInvalidoException;
import com.example.backend_reservashotel.domain.exception.RecursoNoEncontradoException;
import com.example.backend_reservashotel.domain.exception.ReglaNegocioException;
import com.example.backend_reservashotel.infrastructure.controllers.dtos.ActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

// Convierte las excepciones en respuestas ActionResponse con el codigo HTTP correcto
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400: dato invalido detectado en el dominio
    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<ActionResponse<Void>> datoInvalido(DatoInvalidoException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // 400: fallo @Valid en un Request; devuelve { campo: mensaje }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ActionResponse<Map<String, String>>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return respuesta(HttpStatus.BAD_REQUEST, "Datos inválidos", errores);
    }

    // 400: JSON mal formado o tipo de dato incorrecto (ej. texto en un id numerico)
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ActionResponse<Void>> peticionMalFormada(Exception ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "Petición mal formada", null);
    }

    // 401: login fallido (usuario o password incorrectos, o usuario desactivado).
    // Un solo mensaje para no revelar qué usuarios existen.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ActionResponse<Void>> loginFallido(AuthenticationException ex) {
        return respuesta(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos", null);
    }

    // 404: el registro buscado no existe
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ActionResponse<Void>> noEncontrado(RecursoNoEncontradoException ex) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // 404: la ruta no existe
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ActionResponse<Void>> rutaNoExiste(NoResourceFoundException ex) {
        return respuesta(HttpStatus.NOT_FOUND, "Ruta no encontrada", null);
    }

    // 409: se rompe una regla de negocio (duplicado, registro en uso, fechas que chocan...)
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ActionResponse<Void>> reglaNegocio(ReglaNegocioException ex) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    // 500: cualquier otro error; se registra en el log y no se expone el detalle
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ActionResponse<Void>> errorGeneral(Exception ex) {
        log.error("Error no controlado", ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null);
    }

    private static <T> ResponseEntity<ActionResponse<T>> respuesta(HttpStatus estado, String mensaje, T result) {
        return ResponseEntity.status(estado).body(new ActionResponse<>(mensaje, result));
    }
}
