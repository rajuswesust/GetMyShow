package com.raju.getmyshow.booking.dto.response;

import com.raju.getmyshow.booking.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Example JSON:
 * {
 *     "bookingId": 1,
 *     "bookingReference": "BK12234",
 *     "status": "PENDING",
 *     totalAmount: 1234.80,
 *     totalSeats: 3,
 *     expiresAt: "2026-02-24:12:00:00"
 *     seats: [1, 2, 3]
 * }
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long bookingId;
    private String bookingReference;
    private BookingStatus status;

    // Show details
    private Long showId;
    private String eventTitle;
    private LocalDateTime showStartTime;

    // Booking details
    private BigDecimal totalAmount;
    private Long totalSeats;
    private LocalDateTime expiresAt;
    List<BookedSeatDto> seats;


    //Payment URL (for future)
    private String paymentUrl;
}
