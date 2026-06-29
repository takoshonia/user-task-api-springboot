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
class TaskRepositoryTest {//tests the task repository.

    @Autowired//@Autowired annotation is used to inject the task repository into the test class.
    private TaskRepository taskRepository; //dependency injection. It allows for easy configuration of the task repository. it gives us the ability to change the task repository without having to change the code (open for extension, closed for modification)

    @Autowired
    private UserRepository userRepository; //dependency injection. It allows for easy configuration of the user repository. it gives us the ability to change the user repository without having to change the code (open for extension, closed for modification)   

    @BeforeEach//@BeforeEach annotation is used to run a method before each test.
    void setUp() {//sets up the test environment.
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }//It ensures that the test environment is set up correctly and that the data is deleted before the test is run.            

    //Create task, ownership, access denied tests - Positive unit tests. 
    @Test//@Test annotation is used to run a test.
    void findByUserId_returnsOnlyTasksForGivenUser() {//tests the findByUserId method.
        User owner = persistUser("owner@example.com");//persists a user with the email "owner@example.com".
        User other = persistUser("other@example.com");//persists a user with the email "other@example.com".

        Task ownerTask = new Task();//creates a new task.
        ownerTask.setTitle("Owner task");//sets the title of the task.
        ownerTask.setStatus(TaskStatus.TODO);//sets the status of the task.
        ownerTask.setUser(owner);//sets the user of the task.
        taskRepository.save(ownerTask);//saves the task.

        Task otherTask = new Task();//creates a new task.
        otherTask.setTitle("Other task");//sets the title of the task.      
        otherTask.setStatus(TaskStatus.IN_PROGRESS);//sets the status of the task.
        otherTask.setUser(other);//sets the user of the task.
        taskRepository.save(otherTask);//saves the task.

        assertThat(taskRepository.findByUserId(owner.getId())).hasSize(1)//checks if the taskRepository.findByUserId method returns only the tasks for the given user.
                .first()//gets the first task.
                .extracting(Task::getTitle)//checks if the title of the task is "Owner task".
                .isEqualTo("Owner task");
    }

    private User persistUser(String email) {//persists a user with the email "email".
        User user = new User();//creates a new user.
        user.setName("User");//sets the name of the user.
        user.setEmail(email);//sets the email of the user.
        user.setPassword("hash");//sets the password of the user.
        user.setRole(Role.USER);//sets the role of the user.
        return userRepository.save(user);//saves the user.  
    }
}
