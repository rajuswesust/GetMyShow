package com.raju.getmyshow.eventcatalog.domain.entity;

import com.raju.getmyshow.eventcatalog.domain.enums.LayoutType;
import com.raju.getmyshow.eventcatalog.domain.enums.ScreenStatus;
import com.raju.getmyshow.eventcatalog.domain.enums.ScreenType;
import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "screens")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Screen extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_type", nullable = false, length = 50)
    private ScreenType screenType = ScreenType.STANDARD;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @Column(name = "seats_per_row")
    private Integer seatsPerRow;

    @Column(name = "screenFormat", length = 50)
    private String screen_format;

    @Column(name = "sound_system", length = 50)
    private String soundSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout_type", length = 50)
    private LayoutType layoutType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ScreenStatus screenStatus = ScreenStatus.ACTIVE;

    // Business methods
    public boolean isActive() {
        return this.screenStatus == ScreenStatus.ACTIVE;
    }

    public boolean hasCapacityFor(int requestedSeats) {
        return requestedSeats <= this.totalSeats;
    }
}

