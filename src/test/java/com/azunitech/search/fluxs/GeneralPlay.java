package com.azunitech.search.fluxs;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
public class GeneralPlay {
    @Test
    public void j1Test() {
        Mono<String> m = Mono.just("1");
        m.and(Mono.just("2"))
                .doOnEach(x -> log.info(x))
                .subscribe();
    }

    @Test
    public void j2Test() {
        Flux<Integer> ints = Flux.range(1, 4)
                .map(i -> {
                    if (i <= 3) return i;
                    throw new RuntimeException("Got to 4");
                });
        Disposable o = ints.subscribe(System.out::println,
                error -> System.err.println("Error: " + error));

    }

    @Test
    public void j3Test() {
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.doOnNext(log::info).subscribe(ss);
    }
}

@Log4j2
class SampleSubscriber<T> extends BaseSubscriber<T> {
    public void hookOnSubscribe(Subscription subscription) {

        request(1);
    }

    public void hookOnNext(T value) {
        request(1);
    }
}
