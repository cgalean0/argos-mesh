package com.argos.orders.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp,
    String path
) {
    public ErrorResponse(String message, int status, String path) {
        this(message, status, LocalDateTime.now(), path);
    }
}