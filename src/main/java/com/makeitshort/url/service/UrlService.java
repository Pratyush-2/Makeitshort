package com.makeitshort.url.service;

import com.makeitshort.url.entity.UrlMapping;
import com.makeitshort.url.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlService(
            UrlRepository urlRepository,
            ShortCodeGenerator shortCodeGenerator
    ) {
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public UrlMapping shortenUrl(
            String longUrl,
            String customCode,
            LocalDateTime expiresAt
    ) {

        String shortCode;

        if (customCode != null && !customCode.isBlank()) {

            if (urlRepository.existsByShortCode(customCode)) {
                throw new RuntimeException(
                        "This custom code is already taken"
                );
            }

            shortCode = customCode;

        } else {

            shortCode = generateUniqueCode();
        }

        UrlMapping urlMapping =
                new UrlMapping(longUrl, shortCode, expiresAt);

        return urlRepository.save(urlMapping);
    }

    public UrlMapping getUrl(String shortCode) {

        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new RuntimeException("Short URL not found"));
    }

    private String generateUniqueCode() {

        String code = shortCodeGenerator.generate();

        while (urlRepository.existsByShortCode(code)) {
            code = shortCodeGenerator.generate();
        }
        return code;
    }
}