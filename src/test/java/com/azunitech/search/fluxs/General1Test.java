package com.azunitech.search.fluxs;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class General1Test {
  @Test
  public void g21Test() {
    Flux.range(1, 10)
        .doOnRequest((long r) -> System.out.println("request of " + r))
        .subscribe(
            new BaseSubscriber<Integer>() {

              @Override
              public void hookOnSubscribe(Subscription subscription) {
                request(1);
              }

              @Override
              public void hookOnNext(Integer integer) {
                  log.info("Cancelling after having received {}", integer);
                cancel();
              }
            });
  }

  @Test
  public void g22Test() {
    Flux<String> flux =
        Flux.generate(
            () -> 0,
            (Integer state, SynchronousSink<String> sink) -> {
              sink.next("3 x " + state + " = " + 3 * state);
              if (state == 10) sink.complete();
              return state + 1;
            });

    flux.doOnNext(x -> log.info(String.format("%s", x))).subscribe();
  }

  @Test
  public void g23Test() {
    Flux<String> flux =
        Flux.generate(
            AtomicLong::new,
            (AtomicLong state, SynchronousSink<String> sink) -> {
              long i = state.getAndIncrement();
              sink.next("3 x " + i + " = " + 3 * i);
              if (i == 10) {
                sink.complete();
              }
              return state;
            });

    flux.doOnNext(log::info).subscribe();
  }
}
