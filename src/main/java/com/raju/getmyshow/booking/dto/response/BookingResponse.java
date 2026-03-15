package com.raju.getmyshow.booking.dto.response;

import com.raju.getmyshow.booking.entity.BookingStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public class BookingResponse {
    // Maybe payment link will be given in the response for frontend to load the payment page

    Long bookingId;
    String bookingReference;
    BookingStatus status;
    Long showId;
    String eventTitle;
    LocalDateTime showStartTime;
    Long totalSeats;
    BigDecimal totalAmount;
    List<BookedSeatDto> seats;
    LocalDateTime expiresAt;
    LocalDateTime createdAt;
}
