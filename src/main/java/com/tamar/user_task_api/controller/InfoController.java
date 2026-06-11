package com.tamar.user_task_api.controller;

import com.tamar.user_task_api.config.AppSettings;
import com.tamar.user_task_api.dto.response.AppInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
@Tag(name = "Info")
public class InfoController {

    private final AppSettings appSettings;

    public InfoController(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    @Operation(summary = "Get app info")
    @GetMapping
    public AppInfoResponse getInfo() {
        return new AppInfoResponse(
                appSettings.getApplicationTitle(),
                appSettings.getPaginationLimit(),
                appSettings.getContactEmail()
        );
    }
}
