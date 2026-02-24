package com.raju.getmyshow.booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class BookedSeatDto {
    private Long seatInventoryId;
    private String seatLabel;  // "A12", "B5"
    private String seatType;   // "REGULAR", "PREMIUM", "VIP"
    private BigDecimal price;
}
