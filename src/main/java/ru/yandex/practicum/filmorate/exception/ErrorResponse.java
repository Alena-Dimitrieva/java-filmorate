package ru.yandex.practicum.filmorate.exception;

/**
 * Единый формат ответа об ошибке.
 */
public record ErrorResponse(String error, String description) {}