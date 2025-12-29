package com.javabite.app.service;

import com.javabite.app.dto.BookingDTO;
import com.javabite.app.model.BookingStatus;
import com.javabite.app.model.TableBooking;
import com.javabite.app.repository.TableBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private TableBookingRepository bookingRepository;

    // ============================================
    // CUSTOMER BOOKING HISTORY METHODS
    // ============================================

    /**
     * Get all bookings for a customer (history)
     */
    public List<BookingDTO> getCustomerBookingHistory(Long customerId) {
        // ✅ FIXED: Use correct repository method
        List<TableBooking> bookings = bookingRepository.findByCustomer_IdOrderByBookingDateDescBookingTimeDesc(customerId);
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get only active/upcoming bookings for a customer
     */
    public List<BookingDTO> getCustomerActiveBookings(Long customerId) {
        List<TableBooking> bookings = bookingRepository.findByCustomer_IdOrderByBookingDateDescBookingTimeDesc(customerId);
        return bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED ||
                        booking.getStatus() == BookingStatus.ACTIVE)
                .filter(TableBooking::isUpcoming)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get booking by ID (customer must own it)
     */
    public BookingDTO getBookingByIdForCustomer(Long bookingId, Long customerId) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // ✅ FIXED: Use .getCustomer()
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Access denied: Not your booking");
        }

        return convertToDTO(booking);
    }

    /**
     * Cancel booking by customer
     */
    @Transactional
    public void cancelBookingByCustomer(Long bookingId, Long customerId, String reason) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // ✅ FIXED: Use .getCustomer()
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Access denied: Not your booking");
        }

        if (!booking.canBeCancelled()) {
            throw new RuntimeException("Booking cannot be cancelled (Status: " + booking.getStatus() + ")");
        }

        if (!booking.isUpcoming()) {
            throw new RuntimeException("Cannot cancel past bookings");
        }

        BigDecimal refundAmount = calculateRefundAmount(booking);
        booking.cancelWithRefund(reason, customerId, refundAmount);
        bookingRepository.save(booking);
    }

    /**
     * Check if customer can cancel booking
     */
    public boolean canCustomerCancelBooking(Long bookingId, Long customerId) {
        try {
            TableBooking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // ✅ FIXED: Use .getCustomer()
            return booking.getCustomer().getId().equals(customerId) &&
                    booking.canBeCancelled() &&
                    booking.isUpcoming();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get booking statistics for customer
     */
    public Map<String, Object> getCustomerBookingStats(Long customerId) {
        // ✅ FIXED: Use correct repository method
        List<TableBooking> bookings = bookingRepository.findByCustomer_IdOrderByBookingDateDescBookingTimeDesc(customerId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", bookings.size());
        stats.put("upcomingBookings", bookings.stream()
                .filter(TableBooking::isUpcoming).count());
        stats.put("completedBookings", bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED).count());
        stats.put("cancelledBookings", bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED).count());
        stats.put("refundedBookings", bookings.stream()
                .filter(b -> "COMPLETED".equals(b.getRefundStatus())).count());

        return stats;
    }

    // ============================================
    // ADMIN REFUND METHODS
    // ============================================

    /**
     * Cancel booking with refund (admin action)
     */
    @Transactional
    public void cancelBookingWithRefund(Long bookingId, String reason, Long adminId, BigDecimal refundAmount) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.canBeCancelled()) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.cancelWithRefund(reason, adminId, refundAmount);
        bookingRepository.save(booking);
    }

    /**
     * Complete refund processing
     */
    @Transactional
    public void completeRefund(Long bookingId) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.completeRefund();
        bookingRepository.save(booking);
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    /**
     * Calculate refund amount based on booking
     */
    private BigDecimal calculateRefundAmount(TableBooking booking) {
        LocalDateTime bookingDateTime = LocalDateTime.of(
                booking.getBookingDate(),
                LocalTime.parse(booking.getBookingTime())
        );
        LocalDateTime now = LocalDateTime.now();

        long hoursUntilBooking = java.time.Duration.between(now, bookingDateTime).toHours();

        BigDecimal baseRefund = new BigDecimal("20.00");

        if (hoursUntilBooking >= 24) {
            return baseRefund;
        } else if (hoursUntilBooking >= 12) {
            return baseRefund.multiply(new BigDecimal("0.5"));
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Convert TableBooking to BookingDTO
     */
    private BookingDTO convertToDTO(TableBooking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setTableNumber(booking.getTableNumber());
        dto.setBookingDate(booking.getBookingDate());
        dto.setBookingTime(booking.getBookingTime()); // String, not LocalTime
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setSpecialRequests(booking.getSpecialRequests());
        dto.setStatus(booking.getStatus().toString());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        // Refund fields
        dto.setCancellationReason(booking.getCancellationReason());
        dto.setCancelledAt(booking.getCancelledAt());
        dto.setCancelledBy(booking.getCancelledBy());
        dto.setRefundStatus(booking.getRefundStatus());
        dto.setRefundAmount(booking.getRefundAmount());
        dto.setRefundedAt(booking.getRefundedAt());

        // ✅ FIXED: Use .getCustomer()
        if (booking.getCustomer() != null) {
            dto.setCustomerName(booking.getCustomer().getName());
            dto.setCustomerEmail(booking.getCustomer().getEmail());
            dto.setCustomerId(booking.getCustomer().getId());
        }

        return dto;
    }
}