package com.azunitech.search.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan
public class WebConfig {
    @Bean
    public WebClient webClient(){
        return WebClient.create();
    }
}
