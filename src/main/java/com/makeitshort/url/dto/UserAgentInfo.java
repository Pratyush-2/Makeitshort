package com.makeitshort.url.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAgentInfo {

    private String browser;
    private String device;
    private String operatingSystem;
}