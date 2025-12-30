package com.javabite.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"password", "orders", "bookings"})
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_booking_id")
    @JsonIgnoreProperties({"customer", "user"})
    private TableBooking tableBooking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chef_id")
    @JsonIgnoreProperties({"password", "orders"})
    private User chef;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waiter_id")
    @JsonIgnoreProperties({"password", "orders"})
    private User waiter;

    // ✅ Initialize items list
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"order"})
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // ============================================
    // PAYMENT FIELDS (for OrderDTO)
    // ============================================
    @Column(name = "payment_status")
    private String paymentStatus = "PENDING";

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // ============================================
    // ORDER DETAILS (for OrderResponse)
    // ============================================
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax", precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "admin_notes", length = 1000)
    private String adminNotes;

    @Column(name = "auto_assigned")
    private Boolean autoAssigned = false;

    // ============================================
    // TIMESTAMPS
    // ============================================
    @Column(name = "created_at", nullable = false, updatable = false)
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

    // ============================================
    // LIFECYCLE CALLBACKS
    // ============================================
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = "PENDING";
        }
        if (autoAssigned == null) {
            autoAssigned = false;
        }
        if (items == null) {
            items = new ArrayList<>();
        }
        // Calculate totals
        calculateTotals();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    // ============================================
    // BUSINESS LOGIC METHODS
    // ============================================

    /**
     * Add order item safely
     */
    public void addOrderItem(OrderItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        item.setOrder(this);
    }

    /**
     * Remove order item safely
     */
    public void removeOrderItem(OrderItem item) {
        if (this.items != null) {
            this.items.remove(item);
            item.setOrder(null);
        }
    }

    /**
     * Calculate subtotal, tax, and total
     */
    public void calculateTotals() {
        if (items == null || items.isEmpty()) {
            this.subtotal = BigDecimal.ZERO;
            this.tax = BigDecimal.ZERO;
            this.discount = discount != null ? discount : BigDecimal.ZERO;
            return;
        }

        // Calculate subtotal
        this.subtotal = items.stream()
                .map(item -> BigDecimal.valueOf(item.getPriceAtOrder())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate tax (10% of subtotal)
        this.tax = subtotal.multiply(new BigDecimal("0.10"));

        // Ensure discount is set
        if (this.discount == null) {
            this.discount = BigDecimal.ZERO;
        }
    }

    /**
     * Get total (subtotal + tax - discount)
     */
    public BigDecimal getTotal() {
        if (subtotal == null) {
            calculateTotals();
        }
        BigDecimal sub = subtotal != null ? subtotal : BigDecimal.ZERO;
        BigDecimal taxAmount = tax != null ? tax : BigDecimal.ZERO;
        BigDecimal disc = discount != null ? discount : BigDecimal.ZERO;

        return sub.add(taxAmount).subtract(disc);
    }

    /**
     * Get table number from booking
     */
    public Integer getTableNumber() {
        return tableBooking != null ? tableBooking.getTableNumber() : null;
    }

    /**
     * Get items count
     */
    public int getItemsCount() {
        return items != null ? items.size() : 0;
    }
}