package com.tamar.user_task_api.service;

import com.tamar.user_task_api.dto.request.RegisterRequest;
import com.tamar.user_task_api.dto.response.UserResponse;
import com.tamar.user_task_api.entity.Role;
import com.tamar.user_task_api.entity.User;
import com.tamar.user_task_api.exception.DuplicateEmailException;
import com.tamar.user_task_api.repository.UserRepository;
import com.tamar.user_task_api.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Registration and current-user profile. Login itself is in AuthController
 * (AuthenticationManager + session). Register always assigns Role.USER.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        User saved = userRepository.save(user);
        log.info("User registered: {}", saved.getEmail());
        return toResponse(saved);
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
