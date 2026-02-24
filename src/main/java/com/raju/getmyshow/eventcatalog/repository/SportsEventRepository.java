package com.raju.getmyshow.eventcatalog.repository;

import com.raju.getmyshow.eventcatalog.domain.entity.SportsEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportsEventRepository extends JpaRepository<SportsEvent, Long> {
}
