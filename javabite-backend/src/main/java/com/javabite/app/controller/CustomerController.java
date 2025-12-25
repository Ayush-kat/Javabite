package com.javabite.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    /**
     * Customer Dashboard - GET /customer/dashboard
     */
    @GetMapping("/dashboard")
    public String customerDashboard() {
        return "customer-dashboard";
    }

    /**
     * Get customer orders - GET /api/customer/orders
     * Placeholder for future implementation
     */
    @GetMapping("/orders")
    public String getCustomerOrders() {
        return "{ \"message\": \"Customer orders endpoint - to be implemented\" }";
    }
}