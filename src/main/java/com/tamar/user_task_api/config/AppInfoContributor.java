package com.tamar.user_task_api.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class AppInfoContributor implements InfoContributor {

    private final AppSettings appSettings;

    public AppInfoContributor(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("applicationTitle", appSettings.getApplicationTitle())
                .withDetail("paginationLimit", appSettings.getPaginationLimit())
                .withDetail("contactEmail", appSettings.getContactEmail());
    }
}
