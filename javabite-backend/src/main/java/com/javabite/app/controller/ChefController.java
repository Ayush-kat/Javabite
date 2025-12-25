package com.javabite.app.controller;

import com.javabite.app.model.Order;
import com.javabite.app.payload.ApiResponse;
import com.javabite.app.payload.OrderResponse;
import com.javabite.app.service.CustomUserDetails;
import com.javabite.app.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chef")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@PreAuthorize("hasRole('CHEF')")
public class ChefController {

    private final OrderService orderService;

    /**
     * FIXED: Get NEW orders (assigned to chef but still PENDING)
     */
    @GetMapping("/orders/new")
    public ResponseEntity<ApiResponse> getNewOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Order> orders = orderService.getChefNewOrders(userDetails.getId());
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse(true, "New orders retrieved successfully", response)
        );
    }

    /**
     * FIXED: Get IN PROGRESS orders (PREPARING status)
     */
    @GetMapping("/orders/active")
    public ResponseEntity<ApiResponse> getActiveOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Order> orders = orderService.getChefActiveOrders(userDetails.getId());
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse(true, "Active orders retrieved successfully", response)
        );
    }

    /**
     * FIXED: Get completed orders TODAY only
     */
    @GetMapping("/orders/completed-today")
    public ResponseEntity<ApiResponse> getCompletedToday(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Order> orders = orderService.getChefCompletedToday(userDetails.getId());
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse(true, "Today's completed orders retrieved", response)
        );
    }

    /**
     * FIXED: Chef starts preparation
     * Status: PENDING → PREPARING
     */
    @PostMapping("/orders/{orderId}/start")
    public ResponseEntity<ApiResponse> startPreparation(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order order = orderService.startPreparation(orderId, userDetails.getId());
            return ResponseEntity.ok(
                    new ApiResponse(true, "Preparation started",
                            OrderResponse.fromEntity(order))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * FIXED: Chef marks order ready
     * Status: PREPARING → READY
     */
    @PutMapping("/orders/{orderId}/ready")
    public ResponseEntity<ApiResponse> markOrderReady(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order order = orderService.markOrderReady(orderId, userDetails.getId());
            return ResponseEntity.ok(
                    new ApiResponse(true, "Order marked as ready",
                            OrderResponse.fromEntity(order))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}