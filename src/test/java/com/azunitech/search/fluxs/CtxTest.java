package com.azunitech.search.fluxs;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
public class CtxTest {
  @Test
  public void ctx1Test() {
    Flux<String> f =
        Flux.just("1", "2", "3")
            .flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.get("Key"))));
    ConnectableFlux<String> p = f.publish();
    p.contextWrite(c -> c.put("Key", "a")).doOnNext(log::info).subscribe();
    p.connect();
  }

  @Test
  public void ctx2Test(){

  }
}
