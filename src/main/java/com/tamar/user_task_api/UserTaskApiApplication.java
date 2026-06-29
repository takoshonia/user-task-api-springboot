package com.tamar.user_task_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point. Spring Boot scans this package, wires beans (controllers,
 * services, repositories, security), loads application.properties + active profile,
 * and starts the embedded web server on port 8081.
 */
@SpringBootApplication
public class UserTaskApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserTaskApiApplication.class, args);
	}

}
