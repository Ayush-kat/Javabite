package com.javabite.app.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)  // ← Change to EAGER
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"orders", "bookings", "password", "hibernateLazyInitializer"})
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)  // ← Change to EAGER
    @JoinColumn(name = "table_booking_id")
    @JsonIgnoreProperties({"orders", "customer", "hibernateLazyInitializer"})
    private TableBooking tableBooking;

    @ManyToOne(fetch = FetchType.EAGER)  // ← Change to EAGER
    @JoinColumn(name = "chef_id")
    @JsonIgnoreProperties({"orders", "password", "hibernateLazyInitializer"})
    private User chef;

    @ManyToOne(fetch = FetchType.EAGER)  // ← Change to EAGER
    @JoinColumn(name = "waiter_id")
    @JsonIgnoreProperties({"orders", "password", "hibernateLazyInitializer"})
    private User waiter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)  // ← Change to EAGER
    @JsonIgnoreProperties({"order", "hibernateLazyInitializer"})
    @Fetch(FetchMode.SUBSELECT)  // ← ADD THIS to avoid N+1 query problem
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "chef_assigned_at")
    private LocalDateTime chefAssignedAt;

    @Column(name = "waiter_assigned_at")
    private LocalDateTime waiterAssignedAt;

    @Column(name = "preparation_started_at")
    private LocalDateTime preparationStartedAt;

    @Column(name = "ready_at")
    private LocalDateTime readyAt;

    @Column(name = "served_at")
    private LocalDateTime servedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "auto_assigned")
    private boolean autoAssigned = false;
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "payment_status")
    private String paymentStatus = "UNPAID"; // UNPAID, PAID, REFUNDED

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * Add order item to collection
     */
    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Remove order item from collection
     */
    public void removeOrderItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    /**
     * Get table number from booking
     */
    public Integer getTableNumber() {
        return tableBooking != null ? tableBooking.getTableNumber() : null;
    }

    /**
     * Calculate subtotal from all order items
     * ✅ FIXED: Use getPriceAtOrder() instead of getPrice()
     */
    public BigDecimal getSubtotal() {
        return items.stream()
                .map(item -> BigDecimal.valueOf(item.getPriceAtOrder())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate 10% tax on subtotal
     */
    public BigDecimal getTax() {
        return getSubtotal().multiply(BigDecimal.valueOf(0.10));
    }

    /**
     * Get discount (currently always zero)
     */
    public BigDecimal getDiscount() {
        return BigDecimal.ZERO;
    }

    /**
     * Calculate total: subtotal + tax - discount
     */
    public BigDecimal getTotal() {
        return getSubtotal().add(getTax()).subtract(getDiscount());
    }

    /**
     * Auto-set timestamps on create
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Auto-update timestamp on update
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}