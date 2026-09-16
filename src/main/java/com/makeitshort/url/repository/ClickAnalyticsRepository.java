package com.makeitshort.url.repository;

import com.makeitshort.url.entity.ClickAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {
    @Query("""
    SELECT ca.device, COUNT(ca)
    FROM ClickAnalytics ca
    JOIN ca.clickEvent ce
    WHERE ce.urlMapping.id = :urlMappingId
    GROUP BY ca.device
""")
    List<Object[]> countClicksByDevice(Long urlMappingId);

    @Query("""
    SELECT ca.operatingSystem, COUNT(ca)
    FROM ClickAnalytics ca
    JOIN ca.clickEvent ce
    WHERE ce.urlMapping.id = :urlMappingId
    GROUP BY ca.operatingSystem
""")
    List<Object[]> countClicksByOperatingSystem(Long urlMappingId);

    @Query("""
    SELECT ca.browser, COUNT(ca)
    FROM ClickAnalytics ca
    JOIN ca.clickEvent ce
    WHERE ce.urlMapping.id = :urlMappingId
    GROUP BY ca.browser
""")
    List<Object[]> countClicksByBrowser(Long urlMappingId);
}
