package com.javabite.app.payload;

import com.javabite.app.model.BookingStatus;
import com.javabite.app.model.TableBooking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private Integer numberOfGuests;
    private Integer tableNumber;
    private BookingStatus status;
    private String specialRequests;
    private LocalDateTime createdAt;

    public static BookingResponse fromEntity(TableBooking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getName())
                .bookingDate(booking.getBookingDate())
                .bookingTime(LocalTime.parse(booking.getBookingTime()))
                .numberOfGuests(booking.getNumberOfGuests())
                .tableNumber(booking.getTableNumber())
                .status(booking.getStatus())
                .specialRequests(booking.getSpecialRequests())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
