package com.raju.getmyshow.booking.dto.response;

import com.raju.getmyshow.booking.entity.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {

    private Long seatInventoryId;
    private String rowLabel;
    private Integer seatNumber;
    private String displayLabel;  // "A12"
    private String seatType;      // "REGULAR", "PREMIUM"
    private SeatStatus status;    // AVAILABLE, LOCKED, BOOKED
    private BigDecimal price;
    private Boolean isAccessible;
    private Boolean isAisle;
}
