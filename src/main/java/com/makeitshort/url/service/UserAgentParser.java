package com.makeitshort.url.service;

import com.makeitshort.url.dto.UserAgentInfo;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;

@Component
public class UserAgentParser {

    private final UserAgentAnalyzer analyzer =
            UserAgentAnalyzer.newBuilder().build();

    public UserAgentInfo parse(String userAgent) {

        UserAgent parsed = analyzer.parse(userAgent);

        String browser = parsed.getValue("AgentName");
        String device = parsed.getValue("DeviceClass");
        String operatingSystem = parsed.getValue("OperatingSystemName");

        return new UserAgentInfo(
                browser,
                device,
                operatingSystem
        );
    }
}