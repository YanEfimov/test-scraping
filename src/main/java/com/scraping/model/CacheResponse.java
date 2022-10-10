package com.scraping.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CacheResponse {
    private ScrapingResponse scrapingResponse;
    private LocalDateTime dateTime;
}
