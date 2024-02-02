package com.example.code.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    private final int DEFAULT_TIMEOUT = 10000;

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Bean
    public RestTemplate sunriseApiRestTemplate(RestTemplateBuilder builder){
        return builder.setConnectTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
                .build();
    }

    @Bean
    public RestTemplate chatApiRestTemplate(RestTemplateBuilder builder){
        RestTemplate restTemplate = builder.setConnectTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(DEFAULT_TIMEOUT))
                .build();

        restTemplate.getInterceptors().add(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + OPENAI_API_KEY);
            return execution.execute(request, body);
        }));

        return restTemplate;
    }

}
