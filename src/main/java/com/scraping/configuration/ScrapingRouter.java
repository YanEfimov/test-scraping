package com.scraping.configuration;

import com.scraping.handler.ScrapingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class ScrapingRouter {
    @Bean
    public RouterFunction<ServerResponse> route(ScrapingHandler scrapingHandler) {

        return RouterFunctions
                .route(GET("/reviews/{domain}").and(accept(MediaType.APPLICATION_JSON)), scrapingHandler::handle);
    }
}
