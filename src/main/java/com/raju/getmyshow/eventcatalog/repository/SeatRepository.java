package com.raju.getmyshow.eventcatalog.repository;

import com.raju.getmyshow.eventcatalog.domain.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    String findDisplayLabelById(Long seatId);
}
