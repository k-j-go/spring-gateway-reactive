package com.azunitech.search.fluxs;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class SubscriberTest {
  @Test
  public void sub1Test() {
    Flux<String> source = Flux.just("a", "b", "c");
    source.subscribe(
        new BaseSubscriber<String>() {
          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            request(1); // <-- here
          }

          @Override
          protected void hookOnNext(String value) {
            request(2); // <-- here
            System.out.println(value);
          }
        });
  }

  @Test
  public void delayTest() {
    ServiceA serviceA = new ServiceA();
    ErrorHandler errorHandler = new ErrorHandler();

    Flux.fromIterable(getSomeLongList())
        .delayElements(Duration.ofMillis(100))
        .doOnNext(serviceA::someObserver)
        .map((Integer d) -> d * 2)
        .take(3)
        .onErrorResume((Throwable error) -> Mono.just(1))
        .doAfterTerminate(
            new Runnable() {
              @Override
              public void run() {}
            })
        .subscribe(System.out::println);
  }

  private List<Integer> getSomeLongList() {
    return Arrays.asList(1, 2, 3);
  }
}

@Log4j2
class ServiceA {
  public void someObserver(Integer input) {
    log.info(input);
  }
}

@Log4j2
class ErrorHandler {
  public void fallback(Throwable error) {}
}
