package com.azunitech.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

import static org.springframework.http.HttpMethod.GET;

@Log4j2
@Component
public class RouteConfig {

    public interface MyFilters extends UnaryOperator<GatewayFilterSpec> {
    }

    public interface SessionFilters extends UnaryOperator<GatewayFilterSpec> {

    }

    public interface SessionPlayFilter extends UnaryOperator<GatewayFilterSpec> {
    }

    @Autowired
    MyFilters myFilters;

    @Autowired
    SessionFilters sessionFilters;

    @Autowired
    SessionPlayFilter sessionPlayFilter;

    UnaryOperator<GatewayFilterSpec> modififyResponseFromHttpBin = spec -> {
        return spec.modifyResponseBody(String.class, String.class, MediaType.APPLICATION_JSON_VALUE, (ex, str) -> {
            if (ex.getResponse()
                    .getStatusCode()
                    .is2xxSuccessful()) {
                log.info(str);
            }
            return Mono.just(str);
        });
    };

    @Bean
    public RouteLocator routeLocatorPathHttpBin(RouteLocatorBuilder builder, ObjectMapper mapper) {
        return builder.routes()
                .route("pilot", p -> p.path("/get_httpbin")
                        .filters(myFilters.andThen(spec -> modififyResponseFromHttpBin.apply(spec))
                                .andThen(spec -> spec.setPath("/get")))
                        .uri("https://httpbin.org"))
                .build();
    }

    @Bean
    public RouteLocator routeLocatorPathLocalData(RouteLocatorBuilder builder, ObjectMapper mapper) {
        return builder.routes()
                .route("pilot", p -> p.path("/posts")
                        .filters(sessionFilters.andThen(spec -> modififyResponseFromHttpBin.apply(spec))
                                .andThen(spec -> spec.setPath("/posts/{SESSION_ID}")))
                        .uri("http://127.0.0.1:3000"))
                .build();
    }

    @Bean
    public RouteLocator routeLocatorPathLocalController(RouteLocatorBuilder builder, ObjectMapper mapper) {
        return builder.routes()
                .route("posts", p -> p.method(GET)
                        .and()
                        .path("/local/posts")
                        .filters(sessionFilters.andThen(spec -> modififyResponseFromHttpBin.apply(spec))
                                .andThen(spec -> spec.setPath("/local/posts")))
                        .uri("http://127.0.0.1:3000"))
                .build();
    }

    @Bean
    public RouteLocator routeLocatorSample(RouteLocatorBuilder builder, ObjectMapper mapper) {
        return builder.routes()
                .route("play", p -> p.method(GET)
                        .and()
                        .path("/play")
                        .filters(sessionFilters.andThen(sessionPlayFilter)
                                .andThen(spec -> modififyResponseFromHttpBin.apply(spec))
                                .andThen(spec -> spec.setPath("/get")))
                        .uri("https://httpbin.org"))
                .build();
    }

    //http://httpstat.us/
    @Bean
    public RouteLocator routeLocatorHttpstat(RouteLocatorBuilder builder, ObjectMapper mapper) {

        UnaryOperator<GatewayFilterSpec> addHeaders = spec ->
                spec.addRequestHeader("X-HttpStatus-Response-Foo", "bar");

        UnaryOperator<GatewayFilterSpec> addSleep = spec ->
                spec.addRequestParameter("sleep", "1000");

        return builder.routes()
                .route("httpstat", p -> p.method(GET)
                        .and()
                        .method(GET)
                        .and()
                        .path("/httpstat")
                        .filters(sessionFilters.andThen(sessionPlayFilter)
                                .andThen(spec -> modififyResponseFromHttpBin.apply(spec))
                                .andThen(addSleep)
                                .andThen(addHeaders)
                                .andThen(spec -> spec.setPath("/random/200,201,500-504")))
                        .uri("https://httpstat.us"))
                .build();
    }
}
