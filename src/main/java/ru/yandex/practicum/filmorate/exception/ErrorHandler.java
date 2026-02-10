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
import java.util.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Обработка ValidationException из сервиса
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException e) {
        log.warn("ValidationException: {}", e.getMessage());
        Map<String, Object> body = initFilmErrorBody();

        // Если ошибка связана с MPA
        if (e.getMessage().toLowerCase().contains("mpa")) {
            body.put("mpaId", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Ошибки @Valid в теле запроса (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        Map<String, Object> body = initFilmErrorBody();

        // Заполняем поля, по которым есть ошибки
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> {
                    if ("mpaId".equals(fe.getField())) {
                        body.put("mpaId", fe.getDefaultMessage());
                    } else if ("genres".equals(fe.getField())) {
                        body.put("genres", new ArrayList<>());
                    } else {
                        body.put(fe.getField(), fe.getDefaultMessage());
                    }
                });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Ошибки валидации параметров запроса (@RequestParam, @PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("ConstraintViolationException: {}", e.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation failed");

        String description = e.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("");

        body.put("description", description);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Неверный JSON в теле запроса
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());
        Map<String, Object> body = initFilmErrorBody();
        body.put("error", "Malformed request");
        body.put("description", e.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Неверный тип аргумента запроса
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid parameter");
        body.put("description", String.format(
                "Parameter '%s' has invalid value '%s': %s",
                e.getName(), e.getValue(), e.getMessage()
        ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Обработка NotFoundException (MPA, жанр, фильм)
     */
    @ExceptionHandler({NoSuchElementException.class, NotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(Exception e) {
        log.warn("NotFoundException: {}", e.getMessage());
        Map<String, Object> body = initFilmErrorBody();
        body.put("error", "Not found");
        body.put("description", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Ловим IncorrectCountException
     */
    @ExceptionHandler(IncorrectCountException.class)
    public ResponseEntity<Map<String, Object>> handleIncorrectCountException(IncorrectCountException e) {
        log.warn("IncorrectCountException: {}", e.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Incorrect count");
        body.put("description", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Fallback для всех остальных ошибок
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception e) {
        log.error("Unexpected exception: ", e);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Server error");
        body.put("description", "Произошла ошибка сервера: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Инициализация JSON-ответа для Film ошибок
     */
    private Map<String, Object> initFilmErrorBody() {
        Map<String, Object> body = new HashMap<>();
        body.put("id", null);
        body.put("name", null);
        body.put("description", null);
        body.put("releaseDate", null);
        body.put("duration", null);
        body.put("mpaId", null);
        body.put("genres", new ArrayList<>());
        return body;
    }
}
