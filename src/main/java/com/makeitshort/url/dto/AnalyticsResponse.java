package com.makeitshort.url.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@AllArgsConstructor
public class AnalyticsResponse {
    private String url;
    private long totalClicks;
    private Map<String, Long> clicksByDevice;
    private Map<String, Long> clicksByOperatingSystem;
    private Map<String, Long> clicksByBrowser;
    private Map<LocalDate, Long> clicksByDate;


}
