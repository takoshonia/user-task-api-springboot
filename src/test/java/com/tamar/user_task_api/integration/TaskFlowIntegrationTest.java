package com.tamar.user_task_api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void user_canCreateAndListOwnTasks() throws Exception {
        String body = """
                {
                  "title": "Integration task",
                  "description": "Created in test",
                  "status": "TODO"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .with(httpBasic("user@example.com", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration task"))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long taskId = created.get("id").asLong();

        MvcResult listResult = mockMvc.perform(get("/api/tasks")
                        .with(httpBasic("user@example.com", "user123")))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode tasks = objectMapper.readTree(listResult.getResponse().getContentAsString());
        assertThat(tasks.findValuesAsText("id")).contains(String.valueOf(taskId));
    }
}
