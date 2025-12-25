package com.javabite.app.controller;

import com.javabite.app.model.Order;
import com.javabite.app.payload.ApiResponse;
import com.javabite.app.payload.CreateOrderRequest;
import com.javabite.app.payload.OrderResponse;
import com.javabite.app.service.CustomUserDetails;
import com.javabite.app.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@PreAuthorize("isAuthenticated()")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order order = orderService.createOrder(userDetails.getId(), request);
            OrderResponse response = OrderResponse.fromEntity(order);

            log.info("Order created: #{}", order.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Order placed successfully", response));
        } catch (RuntimeException e) {
            log.error("Failed to create order: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Order> orders = orderService.getCustomerOrders(userDetails.getId());
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse(true, "Orders retrieved successfully", response)
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order order = orderService.getOrderById(orderId);

            // Check authorization
            if (userDetails.getAuthorities().stream()
                    .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                            a.getAuthority().equals("ROLE_CHEF")) &&
                    !order.getCustomer().getId().equals(userDetails.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "Access denied"));
            }

            return ResponseEntity.ok(
                    new ApiResponse(true, "Order retrieved successfully",
                            OrderResponse.fromEntity(order))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order order = orderService.cancelOrder(orderId, userDetails.getId());
            return ResponseEntity.ok(
                    new ApiResponse(true, "Order cancelled successfully",
                            OrderResponse.fromEntity(order))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
