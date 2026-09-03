package com.makeitshort.url.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class GeoIpInfo {
    private String country;
    private String region;
    private String city;
}
