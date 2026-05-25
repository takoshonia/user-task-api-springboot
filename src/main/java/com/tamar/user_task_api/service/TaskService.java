package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.TaskCreateRequest;
import com.tamar.user_task_api.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse create(TaskCreateRequest request);

    List<TaskResponse> findAll();

    TaskResponse findById(Long id);

    TaskResponse update(Long id, TaskCreateRequest request);

    void delete(Long id);
}
