package com.raju.getmyshow.booking.repository;

import com.raju.getmyshow.booking.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

}
