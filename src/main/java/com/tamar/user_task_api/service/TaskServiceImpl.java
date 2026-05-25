package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.TaskCreateRequest;
import com.tamar.user_task_api.dto.response.TaskResponse;
import com.tamar.user_task_api.entity.Task;
import com.tamar.user_task_api.entity.User;
import com.tamar.user_task_api.exception.ResourceNotFoundException;
import com.tamar.user_task_api.repository.TaskRepository;
import com.tamar.user_task_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public TaskResponse create(TaskCreateRequest request) {
        User user = findUserById(request.userId());

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setUser(user);

        return toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<TaskResponse> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public TaskResponse findById(Long id) {
        return toResponse(findTaskById(id));
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public TaskResponse update(Long id, TaskCreateRequest request) {
        Task existingTask = findTaskById(id);
        User user = findUserById(request.userId());

        existingTask.setTitle(request.title());
        existingTask.setDescription(request.description());
        existingTask.setStatus(request.status());
        existingTask.setUser(user);

        return toResponse(taskRepository.save(existingTask));
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void delete(Long id) {
        Task existingTask = findTaskById(id);
        taskRepository.delete(existingTask);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getUser().getId()
        );
    }
}
