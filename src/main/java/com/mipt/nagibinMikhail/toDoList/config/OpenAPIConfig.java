package com.mipt.nagibinMikhail.toDoList.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("To-Do List API")
                .version("2.0.0")
                .description("REST API for managing tasks with file attachments, favorites, and preferences")
                .contact(new Contact()
                    .name("Test User")
                    .email("test@yandex.ru")));
    }
}
