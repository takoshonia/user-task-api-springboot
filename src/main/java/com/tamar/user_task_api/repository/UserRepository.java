package com.tamar.user_task_api.repository;

import com.tamar.user_task_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Database access for User entity. Spring generates SQL from method names.
 * Used by UserServiceImpl, AuthServiceImpl, TaskServiceImpl, DataLoader, CustomUserDetailsService.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    /** For update: allow keeping own email, block taking someone else's */
    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByEmail(String email);
}
