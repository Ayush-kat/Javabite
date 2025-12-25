package com.javabite.app.model;

public enum OrderStatus {
    PENDING,      // After customer pays, goes to Admin
    PREPARING,    // Chef started preparation
    READY,        // Chef finished, ready for waiter
    SERVED,       // Waiter served
    COMPLETED,    // Auto-completed after served
    CANCELLED     // Cancelled by customer
}