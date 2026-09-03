package com.makeitshort.url.service;

import com.makeitshort.url.entity.UrlMapping;
import com.makeitshort.url.enums.Expiry;
import com.makeitshort.url.exception.ShortCodeAlreadyExistsException;
import com.makeitshort.url.exception.UrlExpiredException;
import com.makeitshort.url.exception.UrlNotFoundException;
import com.makeitshort.url.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final RedisTemplate<String, UrlMapping> redisTemplate;

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

        UrlMapping cachedMapping =
                redisTemplate.opsForValue().get(shortCode);

        if (cachedMapping != null) {
            System.out.println("CACHE HIT");
            return cachedMapping;
        }

        System.out.println("CACHE MISS");

        UrlMapping mapping = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        if (mapping.getExpiresAt() != null &&
                mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("Url has expired");
        }

        Duration ttl = Duration.between(
                LocalDateTime.now(),
                mapping.getExpiresAt()
        );

        redisTemplate.opsForValue().set(shortCode, mapping, ttl);

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