package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.TaskCreateRequest;
import com.tamar.user_task_api.entity.Role;
import com.tamar.user_task_api.entity.Task;
import com.tamar.user_task_api.entity.TaskStatus;
import com.tamar.user_task_api.entity.User;
import com.tamar.user_task_api.exception.ResourceNotFoundException;
import com.tamar.user_task_api.repository.TaskRepository;
import com.tamar.user_task_api.repository.UserRepository;
import com.tamar.user_task_api.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("user@example.com");
        owner.setPassword("hash");
        owner.setRole(Role.USER);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                new UserPrincipal(owner),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_assignsTaskToCurrentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        Task saved = new Task();
        saved.setId(10L);
        saved.setTitle("Study");
        saved.setDescription("Spring tests");
        saved.setStatus(TaskStatus.TODO);
        saved.setUser(owner);
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskCreateRequest request = new TaskCreateRequest("Study", "Spring tests", TaskStatus.TODO);
        var response = taskService.create(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.userId()).isEqualTo(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void findById_throwsWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_throwsWhenUserDoesNotOwnTask() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        User other = new User();
        other.setId(2L);
        other.setRole(Role.USER);

        Task task = new Task();
        task.setId(5L);
        task.setTitle("Other task");
        task.setStatus(TaskStatus.TODO);
        task.setUser(other);
        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.findById(5L))
                .isInstanceOf(AccessDeniedException.class);
    }
}
