package com.raju.getmyshow.booking.entity;

import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 💡 PURPOSE: Junction table between Booking and SeatInventory
 * - One booking can have multiple seats
 * - Each seat has a price (captured at booking time)
 * - Denormalized seat_label for quick display
 */
@Entity
@Table(name = "booking_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 💡 REFRESHER: @ManyToOne relationships
     * - Many BookingSeats belong to one Booking
     * - FetchType.LAZY: Don't load booking unless accessed
     * - Use LAZY by default, override with JOIN FETCH in queries when needed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * 💡 DESIGN CHOICE: Store seat_inventory_id as Long
     * - seat_inventory is in same module (Booking)
     * - Could use @ManyToOne to SeatInventory
     * - But we're keeping it simple with ID
     */
    @Column(name = "seat_inventory_id", nullable = false)
    private Long seatInventoryId;

    /**
     * 💡 DENORMALIZATION: seat_label
     * - Copied from seat (via seat_inventory)
     * - Why? Fast display without joins
     * - Example: "A12", "B5", "VIP-3"
     */
    @Column(name = "seat_label", nullable = false, length = 20)
    private String seatLabel;

    /**
     * 💡 IMPORTANT: Price captured at booking time
     * - Price can change later
     * - We store what user paid at booking time
     * - Historical accuracy for refunds/reports
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}