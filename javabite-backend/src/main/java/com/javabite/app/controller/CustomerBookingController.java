package com.javabite.app.controller;

import com.javabite.app.dto.BookingDTO;
import com.javabite.app.service.BookingService;
import com.javabite.app.service.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/bookings")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerBookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Get all bookings for logged-in customer (history)
     */
    @GetMapping("/history")
    public ResponseEntity<?> getBookingHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // ✅ FIXED: Use CustomUserDetails.getId()
            List<BookingDTO> bookings = bookingService.getCustomerBookingHistory(userDetails.getId());
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace(); // ✅ DEBUG: Print error to console
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get only active/upcoming bookings
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<BookingDTO> bookings = bookingService.getCustomerActiveBookings(userDetails.getId());
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get single booking details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            BookingDTO booking = bookingService.getBookingByIdForCustomer(id, userDetails.getId());
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Booking not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Cancel booking (customer)
     */
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> requestBody,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String reason = requestBody != null ? requestBody.get("reason") : "Cancelled by customer";

            bookingService.cancelBookingByCustomer(id, userDetails.getId(), reason);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Booking cancelled successfully");
            response.put("refundStatus", "PENDING");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to cancel booking");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get booking statistics for customer
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getBookingStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Map<String, Object> stats = bookingService.getCustomerBookingStats(userDetails.getId());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace(); // ✅ DEBUG: Print error to console
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}