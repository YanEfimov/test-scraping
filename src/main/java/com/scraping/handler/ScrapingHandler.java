package com.scraping.handler;

import com.scraping.model.CacheResponse;
import com.scraping.model.ScrapingResponse;
import org.h2.util.SoftHashMap;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ScrapingHandler {

    @Value("${websiteurl}")
    private String webSiteUrl;

    @Value("${cacheperiod}")
    private Long cachePeriod;

    private SoftHashMap<String, CacheResponse> cache = new SoftHashMap<>();

    public Mono<ServerResponse> handle(ServerRequest request) {
        try {

            ScrapingResponse scrapingResponse;
            String domain = request.pathVariable("domain");

            if (cache.get(domain) != null && cache.get(domain).getDateTime().isAfter(LocalDateTime.now().minusMinutes(cachePeriod))) {
                scrapingResponse = cache.get(domain).getScrapingResponse();
            } else {
                scrapingResponse = getResponseFromWebSite(domain);
                cache.put(domain, new CacheResponse(scrapingResponse, LocalDateTime.now()));
            }

            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(scrapingResponse));
        } catch (HttpStatusException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                return ServerResponse.notFound().build();
            }
            throw new RuntimeException(ex.getMessage());
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private ScrapingResponse getResponseFromWebSite(String domain) throws IOException {
        String blogUrl = webSiteUrl + domain + ".com";
        Document document = Jsoup.connect(blogUrl).get();
        return new ScrapingResponse(getReviewsCount(document), getRating(document));
    }

    private Integer getReviewsCount(Document document) {
        Element element = document.getElementsByAttributeValue("data-reviews-count-typography", "true").first();
        return Integer.parseInt(element.text().replaceAll("[^0-9]", ""));
    }

    private Double getRating(Document document) {
        Element element = document.select("h2:contains(Reviews)").get(0).select("span").first();
        return Double.parseDouble(element.text());
    }

}
