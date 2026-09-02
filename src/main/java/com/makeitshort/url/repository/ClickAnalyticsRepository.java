package com.makeitshort.url.repository;

import com.makeitshort.url.entity.ClickAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {
}
