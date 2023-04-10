package com.azunitech.search.fluxs;

import com.github.javafaker.Faker;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

@Log4j2
public class FluxTest {

    private CountDownLatch latch = new CountDownLatch(1);
    private static String url = "http://localhost:3000";
    private WebClient webClient;
    private Faker faker = new Faker();
    Random rnd = new Random();

    @BeforeEach
    public void setUp() {

        webClient =
                WebClient.builder()
                        .baseUrl(url)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
    }

    @Test
    public void getTest() throws InterruptedException {
        Flux<Author> ans =
                webClient
                        .get()
                        .uri("/posts")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToFlux(Author.class);

        ans.subscribe(log::info, error -> log.info("Error " + error), () -> latch.countDown());
        latch.await();
    }

    @Test
    public void postTest() throws InterruptedException {

        Author author =
                Author.builder()
                        .id(rnd.nextInt(200))
                        .author(faker.artist().name())
                        .title(faker.book().title())
                        .build();
        Mono<Author> ans =
                webClient
                        .post()
                        .uri("/posts")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(author), Author.class)
                        .retrieve()
                        .bodyToMono(Author.class);

        ans.subscribe(log::info, error -> log.info("Error " + error), () -> latch.countDown());
        latch.await();
    }

    @Test
    public void putTest() throws InterruptedException {
        Author author =
                Author.builder().id(100).author(faker.artist().name()).title(faker.book().title()).build();
        Mono<Author> ans =
                webClient
                        .put()
                        .uri("/posts/" + author.getId())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(author), Author.class)
                        .retrieve()
                        .bodyToMono(Author.class);

        ans.subscribe(log::info, error -> log.info("Error " + error), () -> latch.countDown());
        latch.await();
    }

    @Test
    public void deleteTest() throws InterruptedException {
        Author author =
                Author.builder().id(100).author(faker.artist().name()).title(faker.book().title()).build();
        Mono<Void> ans =
                webClient
                        .delete()
                        .uri("/posts/" + author.getId())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(Void.class);

        ans.subscribe(log::info, error -> log.info("Error " + error), () -> latch.countDown());
        latch.await();
    }
}
