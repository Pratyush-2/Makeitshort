package com.makeitshort.url.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClickAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "click_event_id", nullable = false)
    private ClickEvent clickEvent;

    private String country;
    private String region;
    private String city;
    private String device;
    private String browser;
    private String operatingSystem;

}
