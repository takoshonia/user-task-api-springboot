package com.tamar.user_task_api.repository;

import com.tamar.user_task_api.entity.Role;
import com.tamar.user_task_api.entity.Task;
import com.tamar.user_task_api.entity.TaskStatus;
import com.tamar.user_task_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByUserId_returnsOnlyTasksForGivenUser() {
        User owner = persistUser("owner@example.com");
        User other = persistUser("other@example.com");

        Task ownerTask = new Task();
        ownerTask.setTitle("Owner task");
        ownerTask.setStatus(TaskStatus.TODO);
        ownerTask.setUser(owner);
        taskRepository.save(ownerTask);

        Task otherTask = new Task();
        otherTask.setTitle("Other task");
        otherTask.setStatus(TaskStatus.IN_PROGRESS);
        otherTask.setUser(other);
        taskRepository.save(otherTask);

        assertThat(taskRepository.findByUserId(owner.getId())).hasSize(1)
                .first()
                .extracting(Task::getTitle)
                .isEqualTo("Owner task");
    }

    private User persistUser(String email) {
        User user = new User();
        user.setName("User");
        user.setEmail(email);
        user.setPassword("hash");
        user.setRole(Role.USER);
        return userRepository.save(user);
    }
}
