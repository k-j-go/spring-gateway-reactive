package com.azunitech.search.fluxs;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Log4j2
public class General2Test {
  @Test
  public void g12Test() {
    Flux.just("a").handle((x, sink) -> sink.next(x)).doOnNext(log::info).subscribe();
  }

  @Test
  public void g13Test() {
    Flux<String> alphabet =
        Flux.just(-1, 30, 13, 9, 20)
            .handle(
                (i, sink) -> {
                  String letter = alphabet(i);
                  if (letter != null) sink.next(letter);
                });
    alphabet.subscribe(System.out::println);
  }

  private String alphabet(int letterNumber) {
    if (letterNumber < 1 || letterNumber > 26) {
      return null;
    }
    int letterIndexAscii = 'A' + letterNumber - 1;
    return "" + (char) letterIndexAscii;
  }

  @Test
  public void g14Test() throws InterruptedException {
    Mono<String> mono = Mono.just("hello ");
    Thread t =
        new Thread(
            () ->
                mono.map(msg -> msg + "thread ")
                    .subscribe(v -> log.info(v + Thread.currentThread().getName())));
    t.start();
    t.join();
  }

  @Test
  public void g15Test() {
    Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
    final Flux<String> flux = Flux.range(1, 2).map(i -> 10 + i).publishOn(s).map(i -> "value " + i);
    new Thread(() -> flux.subscribe(System.out::println));
  }

  @Test
  public void g16Test() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Flux<String> flux =
        Flux.interval(Duration.ofMillis(250))
            .map(
                input -> {
                  if (input < 3) return "tick " + input;
                  throw new RuntimeException("boom");
                })
            .onErrorReturn("Uh oh");
    flux.doFinally(x -> latch.countDown()).subscribe(System.out::println);
    latch.await();
  }

  @Test
  public void g17Test() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Flux.interval(Duration.ofMillis(250))
        .map(
            input -> {
              if (input < 3) return "tick " + input;
              throw new RuntimeException("boom");
            })
        .retry(1)
        .elapsed()
        .doFinally(x -> latch.countDown())
        .subscribe(System.out::println, System.err::println);
    latch.await();
  }

  @Test
  public void g18Test() {
    Flux<String> flux =
        Flux.<String>error(new IllegalArgumentException())
            .doOnError(e -> log.info(e.getMessage()))
            .retryWhen(Retry.from(companion -> companion.take(2)));
    flux.subscribe();
  }
}
