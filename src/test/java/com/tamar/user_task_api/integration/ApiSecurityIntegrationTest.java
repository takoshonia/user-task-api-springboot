package com.tamar.user_task_api.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest//SpringBootTest is used to test the full application.
@AutoConfigureMockMvc//AutoConfigureMockMvc is used to test the controller.
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

//Full Spring context + security + MockMvc → proves URL security works.
    @Test
    void tasks_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void info_withoutAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationTitle").exists());
    }

    @ParameterizedTest//ParameterizedTest is used to run the same test with different inputs.
    @CsvSource({//CsvSource is used to provide the different inputs.
            "'{}', name",
            "'{\"name\":\"A\",\"email\":\"bad\",\"password\":\"short\"}', email"
    }) //Runs the same test with different invalid JSON → both must return 400 + validationErrors.
    void register_withInvalidPayload_returns400(String body, String field) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors." + field).exists());
    }

    @Test
    void register_withValidPayload_returns201() throws Exception {
        String email = "newuser-" + System.currentTimeMillis() + "@example.com";
        String body = """
                {
                  "name": "New User",
                  "email": "%s",
                  "password": "password123"
                }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
