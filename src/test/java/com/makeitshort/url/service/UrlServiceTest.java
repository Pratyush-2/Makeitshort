package com.makeitshort.url.service;

import com.makeitshort.url.entity.UrlMapping;
import com.makeitshort.url.enums.Expiry;
import com.makeitshort.url.exception.ShortCodeAlreadyExistsException;
import com.makeitshort.url.exception.UrlExpiredException;
import com.makeitshort.url.exception.UrlNotFoundException;
import com.makeitshort.url.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private RedisTemplate<String, UrlMapping> redisTemplate;

    @Mock
    private ValueOperations<String, UrlMapping> valueOperations;

    @InjectMocks
    private UrlService urlService;

    @Test
    void shouldCreateUrlWithCustomCode() {
        String longUrl = "https://www.google.com";
        String customCode = "google";

        when(urlRepository.existsByShortCode(customCode))
                .thenReturn(false);

        UrlMapping savedMapping = new UrlMapping(longUrl, customCode,
                LocalDateTime.now().plusDays(30)
        );
        when(urlRepository.save(any(UrlMapping.class)))
                .thenReturn(savedMapping);

        UrlMapping result =
                urlService.shortenUrl(
                        longUrl,
                        customCode,
                        Expiry.THIRTY_DAYS
                );

        assertThat(result).isNotNull();
        assertThat(result.getLongUrl()).isEqualTo(longUrl);
        assertThat(result.getShortCode()).isEqualTo(customCode);

        verify(urlRepository).existsByShortCode(customCode);
        verify(urlRepository).save(any(UrlMapping.class));

    }

    @Test
    void shouldRejectDuplicateCustomCode() {
        String longUrl = "https://www.google.com";
        String customCode = "google";
        when(urlRepository.existsByShortCode(customCode)).thenReturn(true);

        ShortCodeAlreadyExistsException exception = assertThrows(
                ShortCodeAlreadyExistsException.class,
                () -> urlService.shortenUrl(
                        "https://www.google.com",
                        customCode,
                        Expiry.THIRTY_DAYS
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("This custom code is already taken");

    }

    @Test
    void shouldCreateShortUrlWithGeneratedShortCode() {
        String generatedCode = "aB91x";

        when(shortCodeGenerator.generate()).thenReturn(generatedCode);

        when(urlRepository.existsByShortCode(generatedCode)).thenReturn(false);

        UrlMapping savedMapping =
                new UrlMapping(
                        "https://www.google.com",
                        generatedCode,
                        java.time.LocalDateTime.now().plusDays(30)
                );
        when(urlRepository.save(any(UrlMapping.class)))
                .thenReturn(savedMapping);

        // Act
        UrlMapping result =
                urlService.shortenUrl(
                        "https://www.google.com",
                        null,
                        Expiry.THIRTY_DAYS
                );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getShortCode()).isEqualTo(generatedCode);

        verify(shortCodeGenerator).generate();
        verify(urlRepository).existsByShortCode(generatedCode);
        verify(urlRepository).save(any(UrlMapping.class));
    }

    @Test
    void shouldRetryWhenGeneratedCodeCollides() {

        // Arrange
        when(shortCodeGenerator.generate())
                .thenReturn("abc", "xyz");

        when(urlRepository.existsByShortCode("abc"))
                .thenReturn(false);

        when(urlRepository.existsByShortCode("xyz"))
                .thenReturn(false);

        UrlMapping savedMapping =
                new UrlMapping(
                        "https://www.google.com",
                        "xyz",
                        java.time.LocalDateTime.now().plusDays(30)
                );

        when(urlRepository.save(any(UrlMapping.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"))
                .thenReturn(savedMapping);

        // Act
        UrlMapping result =
                urlService.shortenUrl(
                        "https://www.google.com",
                        null,
                        Expiry.THIRTY_DAYS
                );

        // Assert
        assertThat(result.getShortCode()).isEqualTo("xyz");

        verify(shortCodeGenerator, times(2)).generate();
        verify(urlRepository, times(2)).save(any(UrlMapping.class));
    }

    @Test
    void shouldFailAfterThreeGeneratedCodeCollisions() {

        // Arrange
        when(shortCodeGenerator.generate())
                .thenReturn("abc", "xyz", "123");

        when(urlRepository.existsByShortCode(anyString()))
                .thenReturn(false);

        when(urlRepository.save(any(UrlMapping.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        // Act + Assert
        assertThrows(
                RuntimeException.class,
                () -> urlService.shortenUrl(
                        "https://www.google.com",
                        null,
                        Expiry.THIRTY_DAYS
                )
        );

        verify(shortCodeGenerator, times(3)).generate();
        verify(urlRepository, times(3))
                .save(any(UrlMapping.class));
    }

    @Test
    void shouldDefaultToThirtyDaysWhenExpiryIsNull() {

        // Arrange
        when(urlRepository.existsByShortCode("google"))
                .thenReturn(false);

        UrlMapping savedMapping =
                new UrlMapping(
                        "https://www.google.com",
                        "google",
                        java.time.LocalDateTime.now().plusDays(30)
                );

        when(urlRepository.save(any(UrlMapping.class)))
                .thenReturn(savedMapping);

        // Act
        UrlMapping result =
                urlService.shortenUrl(
                        "https://www.google.com",
                        "google",
                        null
                );

        // Assert
        assertThat(result.getExpiresAt())
                .isAfter(java.time.LocalDateTime.now().plusDays(29));

        assertThat(result.getExpiresAt())
                .isBefore(java.time.LocalDateTime.now().plusDays(31));
    }

    @Test
    void shouldUseSelectedExpiry() {

        // Arrange
        when(urlRepository.existsByShortCode("hour"))
                .thenReturn(false);

        UrlMapping savedMapping =
                new UrlMapping(
                        "https://www.google.com",
                        "hour",
                        java.time.LocalDateTime.now().plusHours(1)
                );

        when(urlRepository.save(any(UrlMapping.class)))
                .thenReturn(savedMapping);

        // Act
        UrlMapping result =
                urlService.shortenUrl(
                        "https://www.google.com",
                        "hour",
                        Expiry.ONE_HOUR
                );

        // Assert
        assertThat(result.getExpiresAt())
                .isAfter(java.time.LocalDateTime.now().plusMinutes(59));

        assertThat(result.getExpiresAt())
                .isBefore(java.time.LocalDateTime.now().plusHours(2));
    }

    @Test
    void shouldFetchFromDatabaseWhenCacheMisses() {
        String shortCode = "google";
        UrlMapping mapping =
                new UrlMapping(
                        "https://www.google.com",
                        shortCode,
                        java.time.LocalDateTime.now().plusDays(30)
                );
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get(shortCode))
                .thenReturn(null);
        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(mapping));

        UrlMapping result = urlService.getUrl(shortCode);

        assertThat(result).isEqualTo(mapping);

        verify(valueOperations).get(shortCode);
        verify(urlRepository).findByShortCode(shortCode);
        verify(valueOperations).set(
                eq(shortCode),
                eq(mapping),
                any(Duration.class)
        );

    }

    @Test
    void shouldReturnUrlFromCacheWhenCacheHits() {
        String shortCode = "google";

        UrlMapping cachedMapping = new UrlMapping("https://www.google.com",
                shortCode,
                LocalDateTime.now().plusDays(30));

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(shortCode)).thenReturn(cachedMapping);

        UrlMapping result = urlService.getUrl(shortCode);

        assertThat(result).isEqualTo(cachedMapping);

        verify(valueOperations).get(shortCode);
        verify(urlRepository, never()).findByShortCode(shortCode);

    }
    @Test
    void shouldThrowExceptionWhenUrlDoesNotExist() {

        // Arrange
        String shortCode = "doesnotexist";

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(shortCode))
                .thenReturn(null);

        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getUrl(shortCode)
        );

        // Database was checked
        verify(urlRepository).findByShortCode(shortCode);

        // Nothing should be cached
        verify(valueOperations, never())
                .set(anyString(), any(UrlMapping.class), any(Duration.class));
    }
    @Test
    void shouldThrowExceptionWhenUrlIsExpired() {

        // Arrange
        String shortCode = "expired";

        UrlMapping expiredMapping =
                new UrlMapping(
                        "https://www.google.com",
                        shortCode,
                        LocalDateTime.now().minusMinutes(10)
                );

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(shortCode))
                .thenReturn(null);

        when(urlRepository.findByShortCode(shortCode))
                .thenReturn(Optional.of(expiredMapping));

        // Act + Assert
        assertThrows(
                UrlExpiredException.class,
                () -> urlService.getUrl(shortCode)
        );

        // Expired URL should not be cached
        verify(valueOperations, never())
                .set(anyString(), any(UrlMapping.class), any(Duration.class));
    }
}