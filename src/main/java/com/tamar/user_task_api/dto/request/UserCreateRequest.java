package com.tamar.user_task_api.dto.request;

import com.tamar.user_task_api.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
//dto = data transfer object
public record UserCreateRequest( //Request DTO — safe input data transfer object, it is used to transfer data from the client to the server.
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        @NotNull(message = "Role is required")
        Role role
) {
}
