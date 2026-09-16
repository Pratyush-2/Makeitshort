package com.makeitshort.url.repository;

import com.makeitshort.url.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    long countByUrlMappingId(Long urlMappingId);
    @Query("""
    SELECT CAST(ce.clickedAt AS date), COUNT(ce)
    FROM ClickEvent ce
    WHERE ce.urlMapping.id = :urlMappingId
    GROUP BY CAST(ce.clickedAt AS date)
    ORDER BY CAST(ce.clickedAt AS date)
""")
    List<Object[]> countClicksByDate(Long urlMappingId);
}
