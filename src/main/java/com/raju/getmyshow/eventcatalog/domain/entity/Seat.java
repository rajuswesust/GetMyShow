package com.raju.getmyshow.eventcatalog.domain.entity;

import com.raju.getmyshow.eventcatalog.domain.enums.SeatType;
import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "row_label", nullable = false, length = 5)
    private String rowLabel;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;  // 1, 2, 3, ...

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false, length = 50)
    private SeatType seatType = SeatType.REGULAR;

    // Physical properties
    @Column(name = "is_accessible")
    private Boolean isAccessible = false;  // Wheelchair accessible

    @Column(name = "is_aisle")
    private Boolean isAisle = false;  // Aisle seat

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 💡 DENORMALIZATION: display_label
     * - Computed value: rowLabel + seatNumber
     * - Example: "A12", "VIP-5"
     * - Why store? Fast display without concatenation
     * - Could also compute in getter: getRowLabel() + getSeatNumber()
     */
    @Column(name = "display_label", length = 10)
    private String displayLabel;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;  // Permanently blocked (broken/maintenance)

    // Business methods
    public boolean isAvailable() {
        return !isBlocked;
    }

    /**
     * 💡 PATTERN: Lifecycle hook
     * - Runs before INSERT
     * - Auto-generates display label
     * set the createdAt field to the current time
     */
    @PrePersist
    public void onPrePersist() {
        if (this.displayLabel == null) {
            this.displayLabel = this.rowLabel + this.seatNumber;
        }
        this.createdAt = LocalDateTime.now();
    }
}
