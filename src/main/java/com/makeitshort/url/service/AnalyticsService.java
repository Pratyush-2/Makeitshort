package com.makeitshort.url.service;

import com.makeitshort.url.dto.AnalyticsResponse;
import com.makeitshort.url.entity.UrlMapping;
import com.makeitshort.url.exception.UrlNotFoundException;
import com.makeitshort.url.repository.ClickAnalyticsRepository;
import com.makeitshort.url.repository.ClickEventRepository;
import com.makeitshort.url.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UrlRepository urlRepository;
    private final ClickEventRepository clickEventRepository;
    private final ClickAnalyticsRepository clickAnalyticsRepository;

    public AnalyticsResponse getAnalytics(String shortCode) {

        UrlMapping urlMapping =
                urlRepository.findByShortCode(shortCode)
                        .orElseThrow(() ->
                                new UrlNotFoundException("Short URL not found"));

        long totalClicks =
                clickEventRepository.countByUrlMappingId(
                        urlMapping.getId()
                );

        List<Object[]> deviceResults =
                clickAnalyticsRepository.countClicksByDevice(
                        urlMapping.getId()
                );
        List<Object[]> osResults =
                clickAnalyticsRepository.countClicksByOperatingSystem(
                        urlMapping.getId()
                );
        List<Object[]> browserResults =
                clickAnalyticsRepository.countClicksByBrowser(
                        urlMapping.getId()
                );
        List<Object[]> dateResults =
                clickEventRepository.countClicksByDate(
                        urlMapping.getId()
                );

        Map<String, Long> clicksByDevice =
                deviceResults.stream()
                        .collect(Collectors.toMap(
                                row -> (String) row[0],
                                row -> (Long) row[1]
                        ));
        Map<String, Long> clicksByOperatingSystem =
                osResults.stream()
                        .collect(Collectors.toMap(
                                row -> (String) row[0],
                                row -> (Long) row[1]
                        ));
        Map<String, Long> clicksByBrowser =
                browserResults.stream()
                        .collect(Collectors.toMap(
                                row -> (String) row[0],
                                row -> (Long) row[1]
                        ));
        Map<LocalDate, Long> clicksByDate =
                dateResults.stream()
                        .collect(Collectors.toMap(
                                row -> ((java.sql.Date) row[0]).toLocalDate(),
                                row -> (Long) row[1]
                        ));

        return new AnalyticsResponse(
                shortCode,
                totalClicks,
                clicksByDevice,
                clicksByOperatingSystem,
                clicksByBrowser,
                clicksByDate
        );
    }
}