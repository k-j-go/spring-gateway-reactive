package com.azunitech.search;

import com.azunitech.search.filters.RequestHashingGatewayFilterFactory;
import com.azunitech.search.filters.SessionGatewayFilter;
import com.azunitech.search.filters.SessionPlayGatewayFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterAdapterBuilder {
    @Bean
    RouteConfig.MyFilters createMyFilters(ObjectMapper mapper, RequestHashingGatewayFilterFactory hashFactory) {
        return spec ->
                spec.filter(hashFactory.apply(new RequestHashingGatewayFilterFactory.Config()));
    }

    @Bean
    RouteConfig.SessionFilters createSessionFilters(SessionGatewayFilter sessionGatewayFilter) {
        return spec ->
                spec.filter(sessionGatewayFilter);
    }

    @Bean
    RouteConfig.SessionPlayFilter createSessionFlayFilters(SessionPlayGatewayFilter sessionPlayFilter) {
        return spec ->
                spec.filter(sessionPlayFilter);
    }

}
