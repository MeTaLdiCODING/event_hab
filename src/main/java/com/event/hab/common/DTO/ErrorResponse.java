package com.event.hab.common.DTO;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        LocalDateTime errorTime
) {

}
