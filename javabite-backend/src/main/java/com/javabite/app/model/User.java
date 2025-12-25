package com.javabite.app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "max_active_orders")
    private Integer maxActiveOrders = 10;

    @Column(name = "current_active_orders")
    private Integer currentActiveOrders = 0;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    // Invitation fields
    @Column(name = "invitation_token", unique = true)
    private String invitationToken;

    @Column(name = "invitation_sent_at")
    private LocalDateTime invitationSentAt;

    @Column(name = "invitation_accepted_at")
    private LocalDateTime invitationAcceptedAt;

    // ✅ ADD THIS FIELD:
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ✅ ADD THIS METHOD:
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Helper methods
    public void incrementActiveOrders() {
        this.currentActiveOrders++;
    }

    public void decrementActiveOrders() {
        if (this.currentActiveOrders > 0) {
            this.currentActiveOrders--;
        }
    }

    public boolean canAcceptOrder() {
        return this.enabled &&
                this.isAvailable &&
                this.currentActiveOrders < this.maxActiveOrders;
    }
}