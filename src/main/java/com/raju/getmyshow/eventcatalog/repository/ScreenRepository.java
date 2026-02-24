package com.raju.getmyshow.eventcatalog.repository;

import com.raju.getmyshow.eventcatalog.domain.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
}
