package com.tamar.user_task_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class UserTaskApiApplicationTests {

	@Test
	@WithMockUser(username = "user@example.com", roles = "USER")
	void contextLoads() {
	}

}
