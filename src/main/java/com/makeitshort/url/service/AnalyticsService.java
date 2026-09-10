package com.makeitshort.url.service;

import com.makeitshort.url.entity.UrlMapping;
import com.makeitshort.url.exception.UrlNotFoundException;
import com.makeitshort.url.repository.ClickEventRepository;
import com.makeitshort.url.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UrlRepository urlRepository;
    private final ClickEventRepository clickEventRepository;

    public long getTotalClicks(String shortCode) {

        UrlMapping urlMapping = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        return clickEventRepository.countByUrlMappingId(
                urlMapping.getId()
        );
    }
}
