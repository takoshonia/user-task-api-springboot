package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.TaskCreateRequest;
import com.tamar.user_task_api.dto.response.TaskResponse;
import com.tamar.user_task_api.entity.Role;
import com.tamar.user_task_api.entity.Task;
import com.tamar.user_task_api.entity.User;
import com.tamar.user_task_api.exception.ResourceNotFoundException;
import com.tamar.user_task_api.repository.TaskRepository;
import com.tamar.user_task_api.repository.UserRepository;
import com.tamar.user_task_api.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public TaskResponse create(TaskCreateRequest request) {
        User owner = getCurrentUser();

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setUser(owner);

        Task saved = taskRepository.save(task);
        log.info("Task created with id {} for user {}", saved.getId(), owner.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<TaskResponse> findAll() {
        List<Task> tasks = isAdmin()
                ? taskRepository.findAll()
                : taskRepository.findByUserId(getCurrentUser().getId());

        return tasks.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public TaskResponse findById(Long id) {
        Task task = findTaskById(id);
        ensureCanAccess(task);
        return toResponse(task);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public TaskResponse update(Long id, TaskCreateRequest request) {
        Task existingTask = findTaskById(id);
        ensureCanAccess(existingTask);

        existingTask.setTitle(request.title());
        existingTask.setDescription(request.description());
        existingTask.setStatus(request.status());

        return toResponse(taskRepository.save(existingTask));
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void delete(Long id) {
        Task existingTask = findTaskById(id);
        ensureCanAccess(existingTask);
        taskRepository.delete(existingTask);
        log.debug("Task deleted with id {}", id);
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AccessDeniedException("Authentication required");
        }
        return userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user no longer exists"));
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.ADMIN.name()));
    }

    private void ensureCanAccess(Task task) {
        if (isAdmin()) {
            return;
        }
        User current = getCurrentUser();
        if (!task.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("You do not have access to this task");
        }
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
