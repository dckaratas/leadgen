package com.senin.leadgen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PlacesClientConfig {

    @Bean
    public RestClient placesRestClient() {
        return RestClient.builder()
                .baseUrl("https://places.googleapis.com/v1")
                .build();
    }
}