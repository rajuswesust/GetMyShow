package com.raju.getmyshow.booking.service;

import com.raju.getmyshow.booking.config.BookingProperties;
import com.raju.getmyshow.booking.dto.request.CreateBookingRequest;
import com.raju.getmyshow.booking.dto.response.AvailableSeatsResponse;
import com.raju.getmyshow.booking.dto.response.BookingResponse;
import com.raju.getmyshow.booking.repository.BookingRepository;
import com.raju.getmyshow.booking.repository.BookingSeatRepository;
import com.raju.getmyshow.booking.repository.SeatInventoryRepository;
import com.raju.getmyshow.eventcatalog.repository.SeatRepository;
import com.raju.getmyshow.eventcatalog.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

   private final BookingProperties bookingProperties;

    @Transactional(readOnly = true)
    public AvailableSeatsResponse getAvailableSeats(Long showId) {
        return null;
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        return null;
    }
}
