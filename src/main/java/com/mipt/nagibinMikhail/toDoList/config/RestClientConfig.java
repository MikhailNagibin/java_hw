package com.mipt.nagibinMikhail.toDoList.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${external.api.url:http://localhost:8080/external/v1}")
    private String externalApiUrl;

    @Value("${external.api.connect-timeout:3000}")
    private int connectTimeout;

    @Value("${external.api.read-timeout:5000}")
    private int readTimeout;

    @Bean
    public RestClient externalRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeout));
        factory.setReadTimeout(Duration.ofMillis(readTimeout));

        return RestClient.builder()
            .baseUrl(externalApiUrl)
            .requestFactory(factory)
            .defaultHeader("User-Agent", "ToDoList-App/1.0")
            .defaultHeader("Accept", "application/json")
            .build();
    }
}
