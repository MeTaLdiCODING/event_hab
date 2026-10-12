package com.event.hab.common.DTO;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String message,
        Map<String, String> details,
        LocalDateTime errorTime
) {
    public ErrorResponse(String message, LocalDateTime errorTime) {
        this(message, null, errorTime);
    }
}