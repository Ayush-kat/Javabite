package com.javabite.app.service;

import com.javabite.app.model.BookingStatus;
import com.javabite.app.model.TableBooking;
import com.javabite.app.model.User;
import com.javabite.app.payload.CreateBookingRequest;
import com.javabite.app.repository.TableBookingRepository;
import com.javabite.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TableBookingService {

    private final TableBookingRepository bookingRepository;
    private final UserRepository userRepository;

    private static final int MAX_TABLES = 20;
    private static final int MAX_BOOKINGS_PER_SLOT = 10;

    /**
     * Get available time slots for a given date with real-time availability
     */
    public Map<String, Boolean> getAvailableSlots(LocalDate date) {
        List<LocalTime> allSlots = new ArrayList<>();

        // Define time slots based on day of week
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        LocalTime startTime;
        LocalTime endTime;

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            startTime = LocalTime.of(8, 0);
            endTime = LocalTime.of(21, 0);
        } else {
            startTime = LocalTime.of(7, 0);
            endTime = LocalTime.of(20, 0);
        }

        while (startTime.isBefore(endTime)) {
            allSlots.add(startTime);
            startTime = startTime.plusHours(1);
        }

        // Check which slots are booked
        List<TableBooking> bookings = bookingRepository.findByBookingDate(date).stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED ||
                        b.getStatus() == BookingStatus.ACTIVE)
                .toList();

        Map<String, Boolean> availability = new HashMap<>();
        for (LocalTime slot : allSlots) {
            long bookedCount = bookings.stream()
                    .filter(b -> b.getBookingTime().equals(slot))
                    .count();

            // Slot is available if less than max bookings
            availability.put(slot.toString(), bookedCount < MAX_BOOKINGS_PER_SLOT);
        }

        return availability;
    }

    @Transactional
    public TableBooking createBooking(Long customerId, CreateBookingRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Check if slot is available
        Map<String, Boolean> availability = getAvailableSlots(request.getBookingDate());

        if (!availability.getOrDefault(request.getBookingTime().toString(), false)) {
            throw new RuntimeException("This time slot is fully booked");
        }

        // Check if customer already has booking for this slot
        List<TableBooking> existingBookings = bookingRepository.findByBookingDate(request.getBookingDate()).stream()
                .filter(b -> b.getCustomer().getId().equals(customerId) &&
                        b.getBookingTime().equals(request.getBookingTime()) &&
                        (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.ACTIVE))
                .toList();

        if (!existingBookings.isEmpty()) {
            throw new RuntimeException("You already have a booking for this time slot");
        }

        // Check availability count
        Long bookingsCount = bookingRepository.countBookingsForSlot(
                request.getBookingDate(),
                request.getBookingTime()
        );

        if (bookingsCount >= MAX_BOOKINGS_PER_SLOT) {
            throw new RuntimeException("This time slot is fully booked. Please choose another time.");
        }

        // Auto-assign table number
        Integer tableNumber = assignTableNumber(request.getBookingDate(), request.getBookingTime());

        TableBooking booking = TableBooking.builder()
                .customer(customer)
                .bookingDate(request.getBookingDate())
                .bookingTime(String.valueOf(request.getBookingTime()))
                .numberOfGuests(request.getNumberOfGuests())
                .tableNumber(tableNumber)
                .status(BookingStatus.CONFIRMED)
                .specialRequests(request.getSpecialRequests())
                .build();

        TableBooking saved = bookingRepository.save(booking);
        log.info("Booking created: #{} for customer: {} on {} at {}",
                saved.getId(), customer.getName(), request.getBookingDate(), request.getBookingTime());

        return saved;
    }

    private Integer assignTableNumber(LocalDate date, LocalTime time) {
        List<TableBooking> bookedSlots = bookingRepository.findByBookingDate(date).stream()
                .filter(b -> false)
                .toList();

        for (int table = 1; true; table++) {
            int finalTable = table;
            bookedSlots.stream()
                    .anyMatch(b -> b.getTableNumber() != null && b.getTableNumber() == finalTable);
            boolean isBooked = false;

            if (!isBooked) {
                return table;
            }
        }

        // All tables booked
    }

    public List<TableBooking> getCustomerBookings(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return bookingRepository.findByCustomerOrderByBookingDateDescBookingTimeDesc(customer);
    }

    public List<TableBooking> getBookingsForDate(LocalDate date) {
        return bookingRepository.findByBookingDate(date).stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.ACTIVE)
                .toList();
    }

    @Transactional
    public TableBooking cancelBooking(Long bookingId, Long customerId) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.ACTIVE) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public boolean isSlotAvailable(LocalDate date, LocalTime time) {
        Long count = bookingRepository.countBookingsForSlot(date, time);
        return count < MAX_BOOKINGS_PER_SLOT;
    }

    /**
     * ✅ Get all bookings (admin view)
     */
    public List<TableBooking> getAllBookings() {
        return bookingRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * ✅ Get bookings by status
     */
    public List<TableBooking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status).stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * ✅ Get booking by ID
     */
    public TableBooking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    /**
     * ✅ Update booking status (admin)
     */
    @Transactional
    public TableBooking updateBookingStatus(Long bookingId, BookingStatus status) {
        TableBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        TableBooking updated = bookingRepository.save(booking);

        log.info("✅ Booking #{} status updated: {} → {}",
                bookingId, booking.getStatus(), status);

        return updated;
    }

    /**
     * ✅ Get booking statistics
     */
    public Map<String, Object> getBookingStats() {
        Map<String, Object> stats = new HashMap<>();

        // Count by status
        stats.put("confirmed", bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        stats.put("active", bookingRepository.countByStatus(BookingStatus.ACTIVE));
        stats.put("completed", bookingRepository.countByStatus(BookingStatus.COMPLETED));
        stats.put("cancelled", bookingRepository.countByStatus(BookingStatus.CANCELLED));

        // Today's bookings
        LocalDate today = LocalDate.now();
        List<TableBooking> todayBookings = bookingRepository.findByBookingDate(today);
        stats.put("todayTotal", todayBookings.size());
        stats.put("todayActive", todayBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED ||
                        b.getStatus() == BookingStatus.ACTIVE)
                .count());

        // Upcoming bookings (next 7 days)
        LocalDate nextWeek = today.plusDays(7);
        long upcomingCount = bookingRepository.findAll().stream()
                .filter(b -> b.getBookingDate().isAfter(today) &&
                        b.getBookingDate().isBefore(nextWeek))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();
        stats.put("upcomingWeek", upcomingCount);

        return stats;
    }


}