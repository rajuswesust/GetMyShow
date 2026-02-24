package com.raju.getmyshow.eventcatalog.repository;

import com.raju.getmyshow.eventcatalog.domain.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {

}
