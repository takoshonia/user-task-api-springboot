package com.tamar.user_task_api.config;

import com.tamar.user_task_api.entity.Role;
import com.tamar.user_task_api.entity.User;
import com.tamar.user_task_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("Admin User", "admin@example.com", "admin123", Role.ADMIN);
        seedUser("Regular User", "user@example.com", "user123", Role.USER);
    }

    private void seedUser(String name, String email, String rawPassword, Role role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        userRepository.save(user);
    }
}
