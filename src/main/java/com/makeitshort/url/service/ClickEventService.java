package com.makeitshort.url.service;

import com.makeitshort.url.entity.ClickEvent;
import com.makeitshort.url.entity.UrlMapping;
import com.makeitshort.url.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClickEventService {
    private final ClickEventRepository clickEventRepository;
    @Async
    public void recordClick(
            UrlMapping urlMapping,
            String ipAddress,
            String userAgent,
            String referer
    ){
        ClickEvent clickEvent = new ClickEvent();

        clickEvent.setUrlMapping(urlMapping);
        clickEvent.setClickedAt(LocalDateTime.now());
        clickEvent.setIpAddress(ipAddress);
        clickEvent.setUserAgent(userAgent);
        clickEvent.setReferer(referer);

        clickEventRepository.save(clickEvent);
    }



}
