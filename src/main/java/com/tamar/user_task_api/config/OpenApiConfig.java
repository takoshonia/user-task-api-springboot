package com.tamar.user_task_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userTaskApiOpenAPI() {
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("User Task API")
                        .version("1.0")
                        .description("REST API for managing users and tasks with Spring Security (USER and ADMIN roles)"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }

    @Bean
    public OperationCustomizer acceptLanguageHeader() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() != null
                    && operation.getParameters().stream()
                    .anyMatch(p -> "Accept-Language".equalsIgnoreCase(p.getName()))) {
                return operation;
            }

            Parameter languageHeader = new Parameter()
                    .in("header")
                    .name("Accept-Language")
                    .description("ka or en")
                    .required(false)
                    .schema(new StringSchema()._default("ka").addEnumItem("ka").addEnumItem("en"));

            operation.addParametersItem(languageHeader);
            return operation;
        };
    }
}
