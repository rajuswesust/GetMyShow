package com.raju.getmyshow.booking.entity;

import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 💡 REFRESHER: @PrePersist hook
 * - Runs BEFORE entity is saved to database
 * - Good for: generating codes, setting defaults
 * - Alternative: Use service layer or DB triggers
 */
@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 50)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * 💡 QR Code data contains:
     * - Ticket number
     * - Show ID
     * - Booking reference
     * - Digital signature
     * - Encoded as JSON and signed
     */
    @Column(name = "qr_code_data", nullable = false, columnDefinition = "TEXT")
    private String qrCodeData;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketStatus status = TicketStatus.VALID;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    /**
     * 💡 CROSS-MODULE REFERENCE: validated_by
     * - References user from User module
     * - Using ID only (not @ManyToOne)
     * - Follows our modular monolith pattern
     */
    @Column(name = "validated_by")
    private Long validatedBy;  // Staff user ID who scanned ticket

    // Business methods
    public void validate(Long staffUserId) {
        if (this.status != TicketStatus.VALID) {
            throw new IllegalStateException("Ticket is not valid for entry");
        }

        this.status = TicketStatus.USED;
        this.validatedAt = LocalDateTime.now();
        this.validatedBy = staffUserId;
    }

    public void cancel() {
        this.status = TicketStatus.CANCELLED;
    }

    public boolean isUsed() {
        return this.status == TicketStatus.USED;
    }
}