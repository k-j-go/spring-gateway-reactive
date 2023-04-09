package com.azunitech.search.reactive;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.function.UnaryOperator;

@Log4j2
public class LambdaPlay {
    @Test
    public void fluxPlay(){
        Flux.fromIterable(Arrays.asList(1,2,3,4)).map( x -> {
            UnaryOperator<Integer> process = y -> y;
            return process.andThen(y -> y).apply(x);
        }).doOnNext( x -> { log.info(x);}).subscribe();
    }
}
