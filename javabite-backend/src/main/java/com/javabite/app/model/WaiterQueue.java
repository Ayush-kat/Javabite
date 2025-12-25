package com.javabite.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waiter_queue")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaiterQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    // ✅ MAKE SURE THIS EXISTS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waiter_id", nullable = false)
    private User waiter;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}