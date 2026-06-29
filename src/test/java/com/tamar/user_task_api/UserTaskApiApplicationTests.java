package com.tamar.user_task_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest//SpringBootTest is used to test the full application.
class UserTaskApiApplicationTests {//smoke test. It ensures that the app starts without crashing.

	@Test//tests the context loading.
	@WithMockUser(username = "user@example.com", roles = "USER")//mocks a user with the username "user@example.com" and the role "USER".
	void contextLoads() {//tests the context loading.
	}//It ensures that the application context is loaded correctly and that the beans are created correctly.
	//Empty test body — if Spring fails to start (bad config, missing bean), test fails. Minimum “app boots” check.
}
