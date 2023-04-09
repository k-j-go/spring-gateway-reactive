package com.azunitech.search.filters;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.bouncycastle.jcajce.provider.digest.SHA512;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.azunitech.search.filters.RequestHashingGatewayFilterFactory.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = RequestHashingGatewayFilterFactoryTest.RequestHashingFilterTestConfig.class)
@AutoConfigureWebTestClient
class RequestHashingGatewayFilterFactoryTest {

    @TestConfiguration
    static class RequestHashingFilterTestConfig {

        @Autowired
        RequestHashingGatewayFilterFactory requestHashingGatewayFilter;

        @Bean(destroyMethod = "stop")
        WireMockServer wireMockServer() {
            WireMockConfiguration options = wireMockConfig().dynamicPort();
            WireMockServer wireMock = new WireMockServer(options);
            wireMock.start();
            return wireMock;
        }

        @Bean
        RouteLocator testRoutes(RouteLocatorBuilder builder, WireMockServer wireMock)
                throws NoSuchAlgorithmException {
            RequestHashingGatewayFilterFactory.Config config = new RequestHashingGatewayFilterFactory.Config();
            config.setAlgorithm("SHA-512");

            GatewayFilter gatewayFilter = requestHashingGatewayFilter.apply(config);
            return builder
                    .routes()
                    .route(predicateSpec -> predicateSpec
                            .path("/post")
                            .filters(spec -> spec.filter(gatewayFilter))
                            .uri(wireMock.baseUrl()))
                    .build();
        }
    }

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    WireMockServer wireMockServer;

    @AfterEach
    void afterEach() {
        wireMockServer.resetAll();
    }

    @Test
    void shouldAddHeaderWithComputedHash() {
        MessageDigest messageDigest = new SHA512.Digest();
        String body = "hello world";
        String expectedHash = Hex.toHexString(messageDigest.digest(body.getBytes()));

        wireMockServer.stubFor(WireMock.post("/post").willReturn(WireMock.ok()));

        webTestClient.post().uri("/post")
                .bodyValue(body)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/post"))
                .withHeader("X-Hash", equalTo(expectedHash)));
    }

    @Test
    void shouldNotAddHeaderIfNoBody() {
        wireMockServer.stubFor(WireMock.post("/post").willReturn(WireMock.ok()));

        webTestClient.post().uri("/post")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/post"))
                .withoutHeader("X-Hash"));
    }
}
