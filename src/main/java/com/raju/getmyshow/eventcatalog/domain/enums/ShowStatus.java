package com.raju.getmyshow.eventcatalog.domain.enums;

public enum ShowStatus {
    SCHEDULED,       // Show scheduled but booking not open
    BOOKING_OPEN,    // Booking is open
    BOOKING_CLOSED,  // Booking closed (show still in future)
    LIVE,            // Show is currently running
    COMPLETED,       // Show has ended
    CANCELLED        // Show cancelled
}