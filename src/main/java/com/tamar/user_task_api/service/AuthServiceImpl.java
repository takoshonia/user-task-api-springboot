package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.RegisterRequest;
import com.tamar.user_task_api.dto.response.UserResponse;
import com.tamar.user_task_api.entity.Role;
import com.tamar.user_task_api.entity.User;
import com.tamar.user_task_api.exception.DuplicateEmailException;
import com.tamar.user_task_api.repository.UserRepository;
import com.tamar.user_task_api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        return toResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public UserResponse getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return toResponse(principal.getUser());
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
