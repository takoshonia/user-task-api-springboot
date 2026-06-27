package com.tamar.user_task_api.controller;

import com.tamar.user_task_api.config.AppSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InfoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AppSettings.class)
@TestPropertySource(properties = {
        "app.settings.application-title=Test API",
        "app.settings.pagination-limit=25",
        "app.settings.contact-email=test@example.com"
})
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getInfo_returnsAppSettings() throws Exception {
        mockMvc.perform(get("/api/info").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationTitle").value("Test API"))
                .andExpect(jsonPath("$.paginationLimit").value(25))
                .andExpect(jsonPath("$.contactEmail").value("test@example.com"));
    }
}
