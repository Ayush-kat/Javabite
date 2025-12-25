package com.javabite.app.model;

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
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_booking_id")
    private TableBooking tableBooking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chef_id")
    private User chef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waiter_id")
    private User waiter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
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