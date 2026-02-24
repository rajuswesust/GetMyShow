package com.raju.getmyshow.booking.entity;

import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_inventory")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatInventory extends BaseEntity {

    @Column(name = "show_id", nullable = false)
    private Long showId;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private SeatStatus seatStatus = SeatStatus.AVAILABLE;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "locked_by", length = 50)
    private String lockedBy;

    @Column(name = "lock_expires_at")
    private LocalDateTime lockExpiresAt;

    @Column(name = "booking_id")
    private Long bookingId;

    //=======================================
    //    Business Methods (Domain logics)
    //======================================

    //is the seat available
    //lock the seat
    //make the seat available or booked or locked
    //is the lock expired

    public void lock(String sessionId, int lockDurationMinutes) {
        if(this.seatStatus  != SeatStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Seat is not available for locking. Current status: " + this.seatStatus
            );
        }
        this.seatStatus = SeatStatus.LOCKED;
        this.lockedAt = LocalDateTime.now();
        this.lockedBy = sessionId;
        this.lockExpiresAt = LocalDateTime.now().plusMonths(lockDurationMinutes);
    }

    public void confirmBooking(Long bookingId) {
        if(this.seatStatus != SeatStatus.LOCKED) {
            throw new IllegalStateException("Seat is not locked. Current status: " + this.seatStatus);
        }
        this.seatStatus = SeatStatus.BOOKED;
        this.bookingId = bookingId;

        //clear lock fields
        this.lockedBy = null;
        this.lockedAt = null;
        this.lockExpiresAt = null;
    }

    public void releaseLock() {
        if (this.seatStatus != SeatStatus.LOCKED) {
            throw new IllegalStateException(
                    "Only locked seats can be released. Current status: " + this.seatStatus
            );
        }

        this.seatStatus = SeatStatus.AVAILABLE;
        this.lockedAt = null;
        this.lockedBy = null;
        this.lockExpiresAt = null;
        this.bookingId = null;
    }

    public boolean isLockExpired() {
        return this.seatStatus == SeatStatus.LOCKED
                && this.lockExpiresAt != null
                && LocalDateTime.now().isAfter(this.lockExpiresAt);
    }

    public boolean isAvailable() {
        return this.seatStatus == SeatStatus.AVAILABLE;
    }

    public boolean isLockedBy(String sessionId) {
        return this.seatStatus == SeatStatus.LOCKED
                && this.lockedBy != null
                && this.lockedBy.equals(sessionId);
    }
 }

