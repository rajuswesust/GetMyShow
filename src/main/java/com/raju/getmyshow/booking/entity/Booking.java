package com.raju.getmyshow.booking.entity;

import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

    @Column(name = "booking_reference", nullable = false, unique = true, length = 50)
    private String bookingReference;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "show_id", nullable = false)
    private Long showId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING bookings can be confirmed. Current status: " + this.status
            );
        }
        if (hasExpired()) {
            throw new IllegalStateException("Cannot confirm expired booking");
        }

        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void expire() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only PENDING bookings can expire");
        }

        this.status = BookingStatus.EXPIRED;
    }

    public void cancel(String reason) {
        if (this.status == BookingStatus.EXPIRED || this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already " + this.status);
        }

        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    public boolean hasExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isConfirmed() {
        return this.status == BookingStatus.CONFIRMED;
    }

    public boolean isPending() {
        return this.status == BookingStatus.PENDING;
    }
}