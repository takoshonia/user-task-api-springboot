package com.tamar.user_task_api.exception;

import java.time.LocalDateTime;
import java.util.Map;

/** Standard JSON error shape returned by GlobalExceptionHandler and security handlers. */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validationErrors
) {
}
