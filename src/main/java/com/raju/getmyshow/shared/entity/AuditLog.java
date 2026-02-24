package com.raju.getmyshow.shared.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Centralized Audit trail for all module
 *
 * Used for:
 * - Security purpose (who did what, when)
 * - Debugging (trace user action)
 * - Compliance (Legal requirements)
 * - Fraud detection (track suspicious behavior)
 *
 * All modules can log to this table
 */

@Entity
@Table(name = "audit_logs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 💡 CROSS-MODULE REFERENCE: user_id
     * - References User from User module
     * - Using ID only (not @ManyToOne)
     * - Can be NULL for system actions
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 💡 ACTION TYPES:
     * Examples:
     * - BOOKING_CREATED
     * - PAYMENT_SUCCESS
     * - SEAT_LOCKED
     * - TICKET_VALIDATED
     * - LOGIN_ATTEMPT
     * - ROLE_ASSIGNED
     */
    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    /**
     * 💡 ENTITY TRACKING:
     * - entity_type: "Booking", "Payment", "Show"
     * - entity_id: ID of affected entity
     *
     * Example: User cancels booking #123
     * - action_type: "BOOKING_CANCELLED"
     * - entity_type: "Booking"
     * - entity_id: 123
     */
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 💡 METADATA: Flexible additional info
     * - Store as JSONB for flexible structure
     * - Can include: IP address, user agent, request details
     *
     * Example:
     * {
     *   "ip": "192.168.1.1",
     *   "user_agent": "Chrome/96.0",
     *   "old_status": "PENDING",
     *   "new_status": "CONFIRMED",
     *   "payment_method": "UPI"
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metaData;

    //================================================
    // Factory Methods
    //=================================================

    public static AuditLog of(Long userId,
                              String actionType,
                              String entityType,
                              Long entityId) {
        return AuditLog.builder()
                .userId(userId)
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .build();
    }

    public static AuditLog withMetaData(Long userId,
                                        String actionType,
                                        String entityType,
                                        Long entityId,
                                        Map<String, Object> metaData) {
        return AuditLog.builder()
                .userId(userId)
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .metaData(metaData)
                .build();
    }
}
