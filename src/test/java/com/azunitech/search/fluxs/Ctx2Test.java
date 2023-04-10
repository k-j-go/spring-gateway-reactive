package com.azunitech.search.fluxs;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Log4j2
public class Ctx2Test {
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  static class Profile {
    long Id;
    String name;
    long postId;
    long tid;
    Post post;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  @Builder
  static class Post {
    long Id;
    String title;
    String author;
    long tid;
  }

  @Test
  public void combineTwo() throws InterruptedException {

    CountDownLatch latch = new CountDownLatch(1);
    HttpClient httpClient =
        HttpClient.create()
            .tcpConfiguration(
                client ->
                    client
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                        .doOnConnected(
                            conn ->
                                conn.addHandlerLast(new ReadTimeoutHandler(10))
                                    .addHandlerLast(new WriteTimeoutHandler(10))));
    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

    WebClient web =
        WebClient.builder().baseUrl("http://localhost:3000").clientConnector(connector).build();

    AtomicInteger count = new AtomicInteger();

    Flux.fromStream(
            IntStream.generate(
                    () -> {
                      int i = count.getAndAdd(1);
                      return (i % 2 == 0) ? 101 : 102;
                    })
                .limit(20)
                .boxed())
        .take(2)
        .flatMap(
            (Integer x) -> {
              return web.get()
                  .uri("profiles/" + x)
                  .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .retrieve()
                  .bodyToMono(Profile.class)
                  .cache()
                  .map(
                      z -> {
                        z.setTid(Thread.currentThread().getId());
                        return z;
                      })
                  .log();
            })
        .flatMap(
            x -> {
              return web.get()
                  .uri("posts/" + x.postId)
                  .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .retrieve()
                  .bodyToMono(Post.class)
                  .cache()
                  .map(
                      p -> {
                        p.setTid(Thread.currentThread().getId());
                        x.setPost(p);
                        return x;
                      })
                  .log();
            })
        .doOnNext(x -> log.info("------- {}", x))
        .doFinally(x -> latch.countDown())
        .subscribe();

    latch.await();
  }
}
