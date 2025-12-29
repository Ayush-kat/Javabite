package com.javabite.app.repository;

import com.javabite.app.model.BookingStatus;
import com.javabite.app.model.TableBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {

    // ============================================
    // CUSTOMER QUERIES (using customer.id)
    // ============================================

    /**
     * Find bookings by customer ID, ordered by date/time descending
     */
    List<TableBooking> findByCustomer_IdOrderByBookingDateDescBookingTimeDesc(Long customerId);

    // ============================================
    // DATE & STATUS QUERIES
    // ============================================

    /**
     * Find bookings by date
     */
    List<TableBooking> findByBookingDate(LocalDate date);

    /**
     * Find bookings by status
     */
    List<TableBooking> findByStatus(BookingStatus status);

    /**
     * Count bookings by status
     */
    Long countByStatus(BookingStatus status);

    // ============================================
    // AVAILABILITY QUERIES
    // ============================================

    /**
     * Count bookings for a specific date and time slot (ACTIVE or CONFIRMED)
     */
    @Query("SELECT COUNT(b) FROM TableBooking b WHERE b.bookingDate = :date AND b.bookingTime = :time " +
            "AND (b.status = 'CONFIRMED' OR b.status = 'ACTIVE')")
    Long countBookingsForSlot(
            @Param("date") LocalDate date,
            @Param("time") String time
    );

    /**
     * Find bookings by table number, date, and status
     * Note: Pass null for tableNumber to get all tables for that date/status
     */
    @Query("SELECT b FROM TableBooking b WHERE " +
            "(:tableNumber IS NULL OR b.tableNumber = :tableNumber) " +
            "AND b.bookingDate = :date " +
            "AND b.status = :status")
    List<TableBooking> findByTableNumberAndBookingDateAndStatus(
            @Param("tableNumber") Integer tableNumber,
            @Param("date") LocalDate date,
            @Param("status") BookingStatus status
    );

    // ============================================
    // REFUND QUERIES
    // ============================================

    /**
     * Find bookings with pending refunds
     */
    @Query("SELECT b FROM TableBooking b WHERE b.refundStatus = 'PENDING' ORDER BY b.cancelledAt ASC")
    List<TableBooking> findPendingRefunds();

    /**
     * Find cancelled bookings with refunds
     */
    @Query("SELECT b FROM TableBooking b WHERE b.status = 'CANCELLED' " +
            "AND b.refundStatus IN ('PENDING', 'COMPLETED') " +
            "ORDER BY b.cancelledAt DESC")
    List<TableBooking> findCancelledBookingsWithRefunds();
}