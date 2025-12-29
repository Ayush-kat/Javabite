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

    // ✅ FIX: Initialize items list to prevent NullPointerException
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"order"})
    @Builder.Default  // ✅ CRITICAL: Ensures Builder also initializes the list
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "payment_status")
    private String paymentStatus = "PENDING";

    @Column(name = "admin_notes", length = 1000)
    private String adminNotes;

    @Column(name = "auto_assigned")
    private Boolean autoAssigned = false;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = "PENDING";
        }
        if (autoAssigned == null) {
            autoAssigned = false;
        }
        // ✅ CRITICAL: Ensure items list is initialized
        if (items == null) {
            items = new ArrayList<>();
        }
    }

    // ✅ FIX: Safe method to add order items
    public void addOrderItem(OrderItem item) {
        // ✅ Ensure list is initialized before adding
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        item.setOrder(this);
    }

    // ✅ FIX: Safe method to remove order items
    public void removeOrderItem(OrderItem item) {
        if (this.items != null) {
            this.items.remove(item);
            item.setOrder(null);
        }
    }

    // Calculate total
    public BigDecimal getTotal() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> BigDecimal.valueOf(item.getPriceAtOrder())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Get items count
    public int getItemsCount() {
        return items != null ? items.size() : 0;
    }
}