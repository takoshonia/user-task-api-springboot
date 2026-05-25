package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.RegisterRequest;
import com.tamar.user_task_api.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    UserResponse getCurrentUser();
}
