package com.makeitshort.url.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class ClickEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "url_mapping_id", nullable = false)
    private UrlMapping urlMapping;

    private LocalDateTime clickedAt;

    private String ipAddress;
    private String userAgent;
    private String referer;
}
