package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Обработка ошибок сервиса (напр., ValidationException)
     * Возвращаем 400 Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        log.warn("ValidationException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation error", e.getMessage()));
    }

    /**
     * Обработка ошибок @Valid при биндинге тела запроса
     *  400 Bad Request и сопоставление полей -> сообщений
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        // Собираем все сообщения в одну строку для удобства клиента / тестов
        String description = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation failed", description));
    }

    /**
     * Обработка ошибок валидации параметров запроса (например, в @RequestParam, @PathVariable)
     * (jakarta.validation.ConstraintViolationException)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("ConstraintViolationException: {}", e.getMessage());
        String description = e.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation failed", description));
    }

    /**
     * Неправильный JSON в теле запроса — возвращаем 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Malformed request", e.getMostSpecificCause().getMessage()));
    }

    /**
     * Неверный тип аргумента (например, нельзя привести pathVar к int)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());
        String desc = String.format("Parameter '%s' has invalid value '%s': %s",
                e.getName(), e.getValue(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid parameter", desc));
    }

    /**
     * Обработка ошибок, когда объект не найден (например, при update unknown)
     * Возвращаем 404 Not Found
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
        log.warn("NoSuchElementException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Not found", e.getMessage()));
    }

    /**
     * Обработка IncorrectCountException — возвращаем 400
     * (оставляем отдельный обработчик, чтобы дать более понятное сообщение)
     */
    @ExceptionHandler(IncorrectCountException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectCountException(IncorrectCountException e) {
        log.warn("IncorrectCountException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Incorrect count", e.getMessage()));
    }

    /**
     * Ловим всё остальное как fallback — 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception e) {
        log.error("Unexpected exception: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Server error", "Произошла ошибка сервера: " + e.getMessage()));
    }
}