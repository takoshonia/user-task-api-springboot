package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.UserCreateRequest;
import com.tamar.user_task_api.dto.request.UserUpdateRequest;
import com.tamar.user_task_api.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(UserCreateRequest request);

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse update(Long id, UserUpdateRequest request);

    void delete(Long id);
}
