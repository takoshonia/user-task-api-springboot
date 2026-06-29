package com.tamar.user_task_api.dto.response;

import com.tamar.user_task_api.entity.Role;
//Response DTO — safe output data transfer object, it is used to transfer data from the database to the client.
//No password. Client only sees what one allow.
public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
}
