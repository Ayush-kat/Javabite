package com.javabite.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "table_bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TableBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ FIX: Map customer_id column
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"password", "orders", "bookings"})
    private User customer;

    // ✅ FIX: Map user_id column (same as customer, for backward compatibility)
    // We'll set this in @PrePersist to always equal customer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "orders", "bookings"})
    private User user;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "booking_time", nullable = false)
    private String bookingTime;

    @Column(name = "number_of_guests")
    private Integer numberOfGuests;

    @Column(name = "table_number")
    private Integer tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "special_requests", length = 500)
    private String specialRequests;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============================================
    // REFUND FIELDS
    // ============================================

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "cancelled_by")
    private Long cancelledBy;

    @Column(name = "refund_status", length = 20)
    private String refundStatus = "NONE";

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    // ============================================
    // LIFECYCLE CALLBACKS
    // ============================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BookingStatus.CONFIRMED;
        }
        if (refundStatus == null) {
            refundStatus = "NONE";
        }
        if (refundAmount == null) {
            refundAmount = BigDecimal.ZERO;
        }
        // ✅ FIX: Set user_id to same as customer_id
        if (customer != null && user == null) {
            user = customer;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ============================================
    // BUSINESS LOGIC METHODS
    // ============================================

    public void cancelWithRefund(String reason, Long cancelledByUserId, BigDecimal amount) {
        this.status = BookingStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledByUserId;
        this.refundStatus = "PENDING";
        this.refundAmount = amount;
    }

    public void completeRefund() {
        if ("PENDING".equals(this.refundStatus)) {
            this.refundStatus = "COMPLETED";
            this.refundedAt = LocalDateTime.now();
        }
    }

    public boolean canBeCancelled() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.ACTIVE;
    }

    public boolean isUpcoming() {
        LocalDateTime bookingDateTime = LocalDateTime.of(bookingDate, LocalTime.parse(bookingTime));
        return bookingDateTime.isAfter(LocalDateTime.now()) &&
                (status == BookingStatus.CONFIRMED || status == BookingStatus.ACTIVE);
    }
}