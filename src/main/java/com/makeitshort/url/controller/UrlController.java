package com.makeitshort.url.controller;

import com.makeitshort.url.dto.CreateUrlRequest;
import com.makeitshort.url.dto.CreateUrlResponse;
import com.makeitshort.url.entity.UrlMapping;
import com.makeitshort.url.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<CreateUrlResponse> shortenUrl(
            @Valid @RequestBody CreateUrlRequest request
    ) {

        UrlMapping savedMapping = urlService.shortenUrl(
                request.getLongUrl(),
                request.getCustomCode(),
                request.getExpiresAt()
        );

        CreateUrlResponse response = new CreateUrlResponse(
                savedMapping.getId(),
                savedMapping.getShortCode(),
                "http://localhost:8080/api/v1/urls/" + savedMapping.getShortCode(),
                savedMapping.getLongUrl(),
                savedMapping.getCreatedAt(),
                savedMapping.getExpiresAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToLongUrl(
            @PathVariable String shortCode
    ) {

        UrlMapping mapping = urlService.getUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(mapping.getLongUrl()))
                .build();
    }
}