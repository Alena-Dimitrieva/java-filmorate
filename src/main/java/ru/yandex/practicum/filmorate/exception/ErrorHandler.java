package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@SuppressWarnings("unused")
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Обработка ошибок валидации из сервиса
     * Возвращение статуса 400 Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        log.warn("ValidationException: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обработка ошибок валидации аннотаций (@Valid)
     * Возвращаем статус 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError ->
                errorResponse.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Обработка ошибок, когда объект не найден (например, при update unknown)
     * Возвращаем статус 404 Not Found
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        log.warn("NoSuchElementException: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Обработка всех остальных неожиданных ошибок
     * Возвращаем статус 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleOtherExceptions(RuntimeException e) {
        log.error("Unexpected exception: ", e);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Произошла ошибка сервера: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}