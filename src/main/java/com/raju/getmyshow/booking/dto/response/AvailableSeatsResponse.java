package com.raju.getmyshow.booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Example JSON:
 * {
 *     eventId: 1,
 *     eventTitle: "XYZ",
 *     showId: 1,
 *     screenId: 1,
 *     totalSeat: 50,
 *     availableSeats: 39,
 *     seats: [{
 *          seatInventoryId: 11,
 *          seatId: 12,
 *          seatType: "REGULAR",
 *          rowNumber: "A",
 *          seatNumber: "1",
 *          displayLabel: "A1"
 *          price: 500.0,
 *          status: "BOOKED"
 *     },
 *     {
 *          seatInventoryId: 12,
 *          seatId: 13
 *          seatType: "REGULAR",
 *          rowNumber: "A",
 *          seatNumber: "2"
 *          displayLabel: "A2"
 *          price: 500.0,
 *          status: "AVAILABLE"
 *     }]
 * }
 */

@Builder
@Data
public class AvailableSeatsResponse {
    private Long showId;
    private String eventTitle;
    private Integer totalSeats;
    private Integer availableSeats;
    private List<SeatDto> seats;

}
