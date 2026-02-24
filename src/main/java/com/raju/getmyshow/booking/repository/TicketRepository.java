package com.raju.getmyshow.booking.repository;

import com.raju.getmyshow.booking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
