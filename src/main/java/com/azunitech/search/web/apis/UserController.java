package com.azunitech.search.web.apis;

import com.azunitech.search.domain.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Log4j2
@RestController
public class UserController {
    private final WebClient webClient;

    public UserController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/local/posts")
    public Flux<User> getPosts() {
        log.info("controller getPosts get called");
        return webClient.get()
                .uri("http://127.0.0.1:3000/posts")
                .retrieve()
                .bodyToFlux( User.class);
    }
}
