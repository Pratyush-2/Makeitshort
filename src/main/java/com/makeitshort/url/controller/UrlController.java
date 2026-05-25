package com.makeitshort.url.controller;

import com.makeitshort.url.model.UrlMapping;
import com.makeitshort.url.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlMapping> shortenUrl(
            @RequestParam String longUrl,
            @RequestParam(required = false) String customSuffix) {
        UrlMapping savedMapping = urlService.shortenUrl(longUrl, customSuffix);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMapping);
    }

    @GetMapping("/{shorturl}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable("shorturl") String shortUrl) {
        Optional<UrlMapping> mappingOptional = urlService.getLongUrl(shortUrl);

        if (mappingOptional.isPresent()) {
            String destinationUrl = mappingOptional.get().getLongUrl();

            // System Design: Trigger a 302 Temporary Redirect to the destination address
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(destinationUrl))
                    .build();
        } else {
            // If the code doesn't exist in our sheet, return an HTTP 404 Error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{shorturl}")
    public ResponseEntity<Map<String, String>> deleteshorturl(@PathVariable("shorturl") String shortUrl){
            urlService.deleteUrl(shortUrl);
            Map<String,String> response = new HashMap<>();
            response.put("message", "Url has been deleted");
            return ResponseEntity.ok(response);
        }
    @PutMapping("/{shorturl}")
    public ResponseEntity<UrlMapping> updateshorturl(@PathVariable("shorturl") String shortUrl, @RequestParam String newLongUrl){
            UrlMapping updatedMapping = urlService.updateLongUrl(shortUrl,newLongUrl);
            return ResponseEntity.ok(updatedMapping);
    }
}
