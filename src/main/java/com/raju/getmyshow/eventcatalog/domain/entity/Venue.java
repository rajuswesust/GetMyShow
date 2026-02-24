package com.raju.getmyshow.eventcatalog.domain.entity;

import com.raju.getmyshow.eventcatalog.domain.enums.VenueStatus;
import com.raju.getmyshow.eventcatalog.domain.enums.VenueType;
import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "venue_type", nullable = false, length = 50)
    private VenueType venueType;

    // Location details
    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String area;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "pincode", length = 10)
    private String pinCode;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country = "BD";

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private java.math.BigDecimal longitude;

    @Column(name = "total_screens", nullable = false)
    private Integer totalScreens = 1;

    @Column(name = "total_capacity")
    private Integer totalCapacity;

    /**
     * 💡 JSONB field example
     * - Stores: ["Parking", "Food Court", "Wheelchair Access"]
     * - In Java: List<String>
     * - In DB: JSONB column
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> facilities;

    // Contact
    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(length = 500)
    private String website;

    // Operational
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private VenueStatus status = VenueStatus.ACTIVE;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    // Business methods
    public boolean isActive() {
        return this.status == VenueStatus.ACTIVE;
    }

    public boolean isOpen(LocalTime time) {
        if (openingTime == null || closingTime == null) {
            return true; // Assume 24/7 if not specified
        }
        return !time.isBefore(openingTime) && !time.isAfter(closingTime);
    }
}