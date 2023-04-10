package com.azunitech.search.fluxs;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Ctx1Test {
    @Test
    public void ctx1Test() {
        Flux<String> f =
                Flux.just("1", "2", "3")
                        .flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.get("Key"))));
        ConnectableFlux<String> p = f.publish();
        p.contextWrite(c -> c.put("Key", "a"))
                .doOnNext(log::info)
                .subscribe();
        p.connect();
    }

    @Test
    public void ctx2Test() {
        List<String> list =
                Stream.of("a", "b")
                        .filter(x -> true)
                        .sorted((o1, o2) -> o1.compareTo(o2))
                        .collect(Collectors.toList());
        log.info(list);

        Flux.fromIterable(list)
                .sort((s1, s2) -> s1.compareTo(s2))
                .doOnNext(x -> log.info(x))
                .subscribe();
    }

    @Test
    public void OKHttpTest() {
        OkHttpClient client = new OkHttpClient();

        Request getRequest = new Request.Builder().url("http://httpstat.us/200")
                .build();

        try {
            Response response = client.newCall(getRequest)
                    .execute();
            System.out.println(response.body()
                    .string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void webClientTestToMono() throws InterruptedException {
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
                WebClient.builder()
                        .baseUrl("http://httpstat.us")
                        .clientConnector(connector)
                        .build();
        Mono<String> post =
                web.get()
                        .uri("/200?sleep=1000")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(String.class);

        post.doOnNext(x -> log.info("---------" + x))
                .doOnSuccess(
                        x -> {
                            latch.countDown();
                        })
                .subscribe();

        latch.await();
    }

    @Test
    public void webClientTestToFlux() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        HttpClient httpClient =
                HttpClient.create()
                        .tcpConfiguration(
                                client ->
                                        client
                                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                                .doOnConnected(
                                                        conn ->
                                                                conn.addHandlerLast(new ReadTimeoutHandler(1))
                                                                        .addHandlerLast(new WriteTimeoutHandler(1))));
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        WebClient web =
                WebClient.builder()
                        .baseUrl("http://httpstat.us")
                        .clientConnector(connector)
                        .build();
        Flux<String> get =
                web.get()
                        .uri("/200?sleep=400")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToFlux(String.class);

        get.doOnNext(x -> log.info("------ {}, {}", x, Thread.currentThread()
                        .getId()))
                .doFinally(x -> latch.countDown())
                .doOnError(err -> log.info(err.getMessage()))
                .subscribe();

        latch.await();
    }

    @Test
    public void testFlux() throws InterruptedException {
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
                WebClient.builder()
                        .baseUrl("http://httpstat.us")
                        .clientConnector(connector)
                        .build();
        Flux.range(4, 2)
                .flatMap(
                        x ->
                                web.get()
                                        .uri("/200")
                                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .retrieve()
                                        .bodyToMono(String.class))
                .doOnNext(x -> log.info("------ {}", x))
                .doFinally(x -> latch.countDown())
                .onErrorContinue((err, x) -> log.info("----- get error {} for {}", err.getMessage(), x))
                .subscribe();
        latch.await();
    }
}
