package com.javabite.app.model;

public enum BookingStatus {
    CONFIRMED,   // Booking confirmed, not yet arrived
    ACTIVE,      // Customer arrived, ordering/dining in progress
    COMPLETED,   // Booking finished, table freed
    CANCELLED,   // Booking cancelled by customer
    NO_SHOW      // Customer didn't show up
}