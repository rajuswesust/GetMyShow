package com.raju.getmyshow.booking.repository;

import com.raju.getmyshow.booking.entity.SeatInventory;
import com.raju.getmyshow.booking.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

    List<SeatInventory> findByShowIdAndSeatStatus(Long showId, SeatStatus seatStatus);
}
