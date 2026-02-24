package com.raju.getmyshow.eventcatalog.domain.entity;


import com.raju.getmyshow.eventcatalog.domain.enums.ShowStatus;
import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Show extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "event_title", nullable = false, length = 255)
    private String eventTitle;  // Copied from event

    @Column(name = "venue_name", nullable = false, length = 255)
    private String venueName;  // Copied from venue (via screen)

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;  // Copied from event ("MOVIE", "SPORTS")

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShowStatus showStatus = ShowStatus.SCHEDULED;

    @Column(name = "booking_opens_at")
    private LocalDateTime bookingOpensAt;

    @Column(name = "booking_closes_at")
    private LocalDateTime bookingClosesAt;

    // =====================================================
    // BUSINESS METHODS
    // =====================================================

    public boolean isBookable() {
        LocalDateTime now = LocalDateTime.now();

        if (showStatus != ShowStatus.BOOKING_OPEN) {
            return false;
        }

        if (bookingOpensAt != null && now.isBefore(bookingOpensAt)) {
            return false;
        }

        if (bookingClosesAt != null && now.isAfter(bookingClosesAt)) {
            return false;
        }

        return availableSeats != null && availableSeats > 0;
    }

    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startTime);
    }

    public boolean hasEnded() {
        return LocalDateTime.now().isAfter(endTime);
    }

    /**
     * 💡 CONCURRENCY CONSIDERATION
     * - This method should be called in transaction
     * - May need pessimistic lock to prevent race conditions
     */
    public void decrementAvailableSeats(int count) {
        if (availableSeats < count) {
            throw new IllegalStateException(
                    "Not enough available seats. Requested: " + count + ", Available: " + availableSeats
            );
        }
        this.availableSeats -= count;
    }

    public void incrementAvailableSeats(int count) {
        this.availableSeats += count;
        if (this.availableSeats > this.totalSeats) {
            this.availableSeats = this.totalSeats;
        }
    }
}
