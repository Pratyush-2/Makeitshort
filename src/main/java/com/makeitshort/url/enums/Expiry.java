package com.makeitshort.url.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


import java.time.Duration;
@Getter
@AllArgsConstructor
public enum Expiry {

    ONE_HOUR(Duration.ofHours(1)),
    ONE_DAY(Duration.ofDays(1)),
    SEVEN_DAYS(Duration.ofDays(7)),
    THIRTY_DAYS(Duration.ofDays(30)),
    NINETY_DAYS(Duration.ofDays(90));

    private final Duration duration;

}