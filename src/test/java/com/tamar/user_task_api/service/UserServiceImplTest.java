package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.UserCreateRequest;
import com.tamar.user_task_api.entity.Role;
import com.tamar.user_task_api.entity.User;
import com.tamar.user_task_api.exception.DuplicateEmailException;
import com.tamar.user_task_api.exception.ResourceNotFoundException;
import com.tamar.user_task_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)//Enables Mockito
class UserServiceImplTest {

    @Mock//@Mock annotation is used to create a mock object. Fake repository/encoder — no real DB
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks //Real UserServiceImpl with mocks injected
    private UserServiceImpl userService;

    @Test //positive unit test
    void create_persistsAdminCreatedUser() {
        UserCreateRequest request = new UserCreateRequest("Admin", "admin2@example.com", "password123", Role.ADMIN);
        //arrange - set up the mock objects and the expected repo behavior
        when(userRepository.existsByEmail("admin2@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hash");

        //act, call the method under test
        User saved = new User();
        saved.setId(2L);
        saved.setName("Admin");
        saved.setEmail("admin2@example.com");
        saved.setRole(Role.ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        var response = userService.create(request);
        //assert, verify the result
        assertThat(response.email()).isEqualTo("admin2@example.com");
        assertThat(response.role()).isEqualTo(Role.ADMIN);
    }

    @Test //negative unit test, Checks: duplicate email → exception, save never called.
    void create_throwsWhenEmailExists() {
        UserCreateRequest request = new UserCreateRequest("Admin", "admin2@example.com", "password123", Role.ADMIN);
        //arrange - set up the mock objects and the expected repo behavior
        when(userRepository.existsByEmail("admin2@example.com")).thenReturn(true);// means the email already exists in the database

        //act, call the method under test
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(DuplicateEmailException.class);

        //assert, verify the result
        verify(userRepository, never()).save(any());
    }

    @Test //negative unit test
    void findById_throwsWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());// means the user is not found in the database

        assertThatThrownBy(() -> userService.findById(99L))//throws a ResourceNotFoundException if the user is not found in the database
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
