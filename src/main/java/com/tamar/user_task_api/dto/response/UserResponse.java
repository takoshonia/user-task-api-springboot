package com.tamar.user_task_api.dto.response;

import com.tamar.user_task_api.entity.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
}
