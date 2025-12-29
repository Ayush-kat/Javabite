package com.javabite.app.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long id;
    private Integer tableNumber;
    private LocalDate bookingDate;
    private String bookingTime;
    private Integer numberOfGuests;
    private String specialRequests;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Customer info
    private Long customerId;
    private String customerName;
    private String customerEmail;

    // Refund fields
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private Long cancelledBy;
    private String refundStatus;
    private BigDecimal refundAmount;
    private LocalDateTime refundedAt;
}