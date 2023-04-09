package com.azunitech.search.filters;

import com.github.javafaker.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static com.azunitech.search.constants.SESSION_ID;
import static com.azunitech.search.constants.SESSION_PLAY_ID;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.putUriTemplateVariables;

@Component
public class SessionPlayGatewayFilter implements GatewayFilter, Ordered {
    @Autowired
    Artist artist;

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String session_id = artist.name();
        exchange.getAttributes()
                .put(SESSION_PLAY_ID, session_id);
        putUriTemplateVariables(exchange, Collections.singletonMap(SESSION_PLAY_ID, session_id));
        return chain.filter(exchange);
    }
}
