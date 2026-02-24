package com.raju.getmyshow.eventcatalog.repository;

import com.raju.getmyshow.eventcatalog.domain.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
}
