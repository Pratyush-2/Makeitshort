package com.makeitshort.url.service;

import com.makeitshort.url.entity.UrlMapping;
import com.makeitshort.url.enums.Expiry;
import com.makeitshort.url.exception.ShortCodeAlreadyExistsException;
import com.makeitshort.url.exception.UrlExpiredException;
import com.makeitshort.url.exception.UrlNotFoundException;
import com.makeitshort.url.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;



    public UrlMapping shortenUrl(
            String longUrl,
            String customCode,
            Expiry expiry
    ) {

        String shortCode;

        if (customCode != null && !customCode.isBlank()) {

            if (urlRepository.existsByShortCode(customCode)) {
                throw new ShortCodeAlreadyExistsException(
                        "This custom code is already taken"
                );
            }

            shortCode = customCode;

        } else {

            shortCode = generateUniqueCode();
        }

        Expiry effectiveExpiry = determineExpiry(expiry);

        LocalDateTime expiresAt =
                LocalDateTime.now().plus(effectiveExpiry.getDuration());

        UrlMapping urlMapping =
                new UrlMapping(longUrl, shortCode, expiresAt);

        return urlRepository.save(urlMapping);
    }

    private Expiry determineExpiry(Expiry expiry) {

        if (expiry == null) {
            return Expiry.THIRTY_DAYS;
        }

        return expiry;
    }

    public UrlMapping getUrl(String shortCode) {

        UrlMapping mapping = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        if (mapping.getExpiresAt() != null &&
                mapping.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new UrlExpiredException("Url has expired");
        }

        return mapping;
    }

    private String generateUniqueCode() {

        String code = shortCodeGenerator.generate();

        while (urlRepository.existsByShortCode(code)) {
            code = shortCodeGenerator.generate();
        }

        return code;
    }
}