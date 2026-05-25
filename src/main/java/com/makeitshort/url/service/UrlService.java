package com.makeitshort.url.service;

import com.makeitshort.url.model.UrlMapping;
import com.makeitshort.url.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final SecureRandom random = new SecureRandom();

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }
    public UrlMapping shortenUrl(String longUrl,String customSuffix){
        String finalshortUrl;
        if(customSuffix!=null && !customSuffix.trim().isEmpty()){
            if (urlRepository.findByShortUrl(customSuffix).isPresent()) {
                throw new RuntimeException("This custom url is already taken");
            }
            finalshortUrl = customSuffix;
        }
        else {
            finalshortUrl = generateRandomCode();
        }
        UrlMapping urlMapping = new UrlMapping(longUrl, finalshortUrl);
        return urlRepository.save(urlMapping);
    }
    public Optional<UrlMapping> getLongUrl(String shortUrl){
        return urlRepository.findByShortUrl(shortUrl);
    }
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(ALPHABET.length());
            code.append(ALPHABET.charAt(randomIndex));
        }
        return code.toString();
    }


}
