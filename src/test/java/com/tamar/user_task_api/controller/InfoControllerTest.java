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

@WebMvcTest(InfoController.class)//Load only this controller (not full app). 
@AutoConfigureMockMvc(addFilters = false) //Skip security filters (avoid 401 in test), MockMvc Simulate HTTP without starting browser
@Import(AppSettings.class)
@TestPropertySource(properties = { //TestPropertySource is used to set the test properties.
        "app.settings.application-title=Test API",
        "app.settings.pagination-limit=25",
        "app.settings.contact-email=test@example.com"
})
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;//MockMvc is a mock object of the MockMvc class. It is used to test the controller.

    @Test
    void getInfo_returnsAppSettings() throws Exception {
        mockMvc.perform(get("/api/info").accept(MediaType.APPLICATION_JSON))//performs a GET request to the /api/info endpoint.
                .andExpect(status().isOk())//checks if the status is OK.
                .andExpect(jsonPath("$.applicationTitle").value("Test API"))//checks if the application title is Test API. 
                .andExpect(jsonPath("$.paginationLimit").value(25))//checks if the pagination limit is 25.
                .andExpect(jsonPath("$.contactEmail").value("test@example.com"));//checks if the contact email is test@example.com.
    }//It ensures that the app settings are returned correctly.
}
