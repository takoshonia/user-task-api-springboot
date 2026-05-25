package com.tamar.user_task_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Email(message = "Email must be valid")
        @Size(max = 120, message = "Email cannot exceed 120 characters")
        String email
) {
}
