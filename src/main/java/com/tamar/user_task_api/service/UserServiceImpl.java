package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.UserCreateRequest;
import com.tamar.user_task_api.dto.request.UserUpdateRequest;
import com.tamar.user_task_api.dto.response.UserResponse;
import com.tamar.user_task_api.entity.User;
import com.tamar.user_task_api.exception.DuplicateEmailException;
import com.tamar.user_task_api.exception.ResourceNotFoundException;
import com.tamar.user_task_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        return toResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return toResponse(findUserById(id));
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest request) {
        User existingUser = findUserById(id);
        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateEmailException(request.email());
        }

        existingUser.setName(request.name());
        existingUser.setEmail(request.email());
        return toResponse(userRepository.save(existingUser));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        User existingUser = findUserById(id);
        userRepository.delete(existingUser);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
