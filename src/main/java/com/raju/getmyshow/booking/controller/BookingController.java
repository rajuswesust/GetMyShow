package com.raju.getmyshow.booking.controller;

import com.raju.getmyshow.booking.dto.request.CreateBookingRequest;
import com.raju.getmyshow.booking.dto.response.AvailableSeatsResponse;
import com.raju.getmyshow.booking.dto.response.BookingResponse;
import com.raju.getmyshow.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<AvailableSeatsResponse> getAvailableSeats(@PathVariable Long showId) {

        log.info("GET /api/shows/{}/seats", showId);

        AvailableSeatsResponse response = bookingService.getAvailableSeats(showId);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/bookings
     *
     * Create a new booking
     * This will lock the seats temporarily (15 min expiry)
     */
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        log.info("POST /api/bookings - User: {}, Show: {}, Seats: {}",
                request.getUserId(), request.getShowId(), request.getSeatInventoryIds());

        BookingResponse response = bookingService.createBooking(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
