package com.raju.getmyshow.booking.repository;

import com.raju.getmyshow.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
