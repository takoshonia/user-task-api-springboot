package com.tamar.user_task_api.repository;

import com.tamar.user_task_api.entity.Role;
import com.tamar.user_task_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest//DataJpaTest is used to test the data access layer for the user repository. Uses real JPA + in-memory test DB (not Mockito)
class UserRepositoryTest {
//Tests that existsByEmail, findByEmail, existsByEmailAndIdNot actually work with SQL
    @Autowired
    private UserRepository userRepository;

    @BeforeEach//@BeforeEach cleans DB before each test — tests don’t affect each other
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void existsByEmail_returnsTrueWhenEmailPresent() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("hash");
        user.setRole(Role.USER);
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }

    @Test
    void findByEmail_returnsUserWhenFound() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("findme@example.com");
        user.setPassword("hash");
        user.setRole(Role.USER);
        userRepository.save(user);

        assertThat(userRepository.findByEmail("findme@example.com"))
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("Test User");
    }

    @Test
    void existsByEmailAndIdNot_detectsDuplicateForOtherUser() {
        User first = new User();
        first.setName("First");
        first.setEmail("first@example.com");
        first.setPassword("hash");
        first.setRole(Role.USER);
        first = userRepository.save(first);

        User second = new User();
        second.setName("Second");
        second.setEmail("second@example.com");
        second.setPassword("hash");
        second.setRole(Role.USER);
        second = userRepository.save(second);

        assertThat(userRepository.existsByEmailAndIdNot("first@example.com", second.getId())).isTrue();
        assertThat(userRepository.existsByEmailAndIdNot("first@example.com", first.getId())).isFalse();
    }
}
