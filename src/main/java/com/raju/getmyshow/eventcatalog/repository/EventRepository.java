package com.raju.getmyshow.eventcatalog.repository;

import com.raju.getmyshow.eventcatalog.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

}
