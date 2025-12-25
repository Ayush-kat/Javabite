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
@RequestMapping("/api/waiter")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
@PreAuthorize("hasRole('WAITER')")
public class WaiterController {

    private final OrderService orderService;

    @GetMapping("/orders/ready")
    public ResponseEntity<ApiResponse> getReadyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Order> orders = orderService.getWaiterReadyOrders(userDetails.getId());
            List<OrderResponse> response = orders.stream()
                    .map(OrderResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse(true, "Ready orders retrieved", response)
            );
        } catch (Exception e) {
            log.error("Error fetching ready orders: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * FIXED: Changed to PUT and using waiterMarkServed method
     */
    @PutMapping("/orders/{orderId}/serve")
    public ResponseEntity<ApiResponse> markAsServed(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order order = orderService.waiterMarkServed(orderId, userDetails.getId());

            return ResponseEntity.ok(
                    new ApiResponse(true, "Order marked as SERVED and COMPLETED",
                            OrderResponse.fromEntity(order))
            );
        } catch (RuntimeException e) {
            log.error("Error marking order as served: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * ✅ Get ALL assigned orders (preparing + ready)
     */
    @GetMapping("/orders/assigned")
    public ResponseEntity<ApiResponse> getAssignedOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Order> orders = orderService.getWaiterAssignedOrders(userDetails.getId());
            List<OrderResponse> response = orders.stream()
                    .map(OrderResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse(true, "Assigned orders retrieved", response)
            );
        } catch (Exception e) {
            log.error("Error fetching assigned orders: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * ✅ Get preparing orders (chef is working on them)
     */
    @GetMapping("/orders/preparing")
    public ResponseEntity<ApiResponse> getPreparingOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Order> orders = orderService.getWaiterPreparingOrders(userDetails.getId());
            List<OrderResponse> response = orders.stream()
                    .map(OrderResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse(true, "Preparing orders retrieved", response)
            );
        } catch (Exception e) {
            log.error("Error fetching preparing orders: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}