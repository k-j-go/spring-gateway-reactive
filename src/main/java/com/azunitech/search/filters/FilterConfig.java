package com.azunitech.search.filters;

import com.github.javafaker.Artist;
import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.function.UnaryOperator;

@Configuration
@ComponentScan
public class FilterConfig {
    @Bean
    Artist createArtist() {
        Faker faker = new Faker();
        Artist artist = faker.artist();
        return artist;
    }
}
