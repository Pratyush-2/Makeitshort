package com.makeitshort.url.repository;

import com.makeitshort.url.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    long countByUrlMappingId(Long urlMappingId);
}
