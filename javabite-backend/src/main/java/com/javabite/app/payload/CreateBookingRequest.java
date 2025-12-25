package com.javabite.app.payload;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateBookingRequest {
    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date must be today or in the future")
    private LocalDate bookingDate;

    @NotNull(message = "Booking time is required")
    private LocalTime bookingTime;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest required")
    @Max(value = 20, message = "Maximum 20 guests allowed")
    private Integer numberOfGuests;

    /**
     * ✅ NEW: Table number field (1-6)
     */
    @NotNull(message = "Table number is required")
    @Min(value = 1, message = "Table number must be between 1 and 6")
    @Max(value = 6, message = "Table number must be between 1 and 6")
    private Integer tableNumber;

    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;
}