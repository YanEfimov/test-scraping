package com.scraping.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ScrapingResponse {
    private Integer reviewsCount;
    private Double rating;
}
