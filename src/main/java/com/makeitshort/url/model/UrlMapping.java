package com.makeitshort.url.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String longUrl;

    @Column(nullable = false)
    private String shortUrl;

    private LocalDateTime createdAt;

    public UrlMapping() {

    }
    public UrlMapping(String longUrl, String shortCode) {
        this.longUrl = longUrl;
        this.shortUrl = shortCode;
        this.createdAt = LocalDateTime.now();
    }
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongUrl() {
        return this.longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortCode() {
        return this.shortUrl;
    }

    public void setShortCode(String shortCode) {
        this.shortUrl = shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}


