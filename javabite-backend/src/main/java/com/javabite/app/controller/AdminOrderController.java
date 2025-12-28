package com.javabite.app.controller;

import com.javabite.app.dto.OrderDTO;
import com.javabite.app.model.Order;
import com.javabite.app.payload.ApiResponse;
import com.javabite.app.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * Get all orders (for history page)
     * GET /api/admin/orders/all
     *
     * ✅ FIXED: Returns DTOs instead of entities to avoid Hibernate proxy issues
     */
    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        try {
            log.info("Admin fetching all orders");
            List<Order> orders = orderService.getAllOrders();

            // Convert to DTOs to avoid Hibernate proxy serialization issues
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(OrderDTO::fromEntity)
                    .collect(Collectors.toList());

            log.info("Retrieved {} orders", orderDTOs.size());
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            log.error("Failed to fetch all orders", e);
            throw new RuntimeException("Failed to fetch orders: " + e.getMessage());
        }
    }

    /**
     * Get order by ID with full details
     * GET /api/admin/orders/{orderId}
     *
     * ✅ FIXED: Returns DTO instead of entity
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderByIdAdmin(@PathVariable Long orderId) {
        try {
            log.info("Admin fetching order details for order ID: {}", orderId);
            Order order = orderService.getOrderByIdForAdmin(orderId);

            // Convert to DTO
            OrderDTO orderDTO = OrderDTO.fromEntity(order);

            return ResponseEntity.ok(orderDTO);
        } catch (Exception e) {
            log.error("Failed to fetch order {}", orderId, e);
            throw new RuntimeException("Failed to fetch order: " + e.getMessage());
        }
    }

    /**
     * Cancel order (admin)
     * PUT /api/admin/orders/{orderId}/cancel
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId) {
        try {
            log.info("Admin cancelling order ID: {}", orderId);
            orderService.cancelOrderByAdmin(orderId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Order cancelled successfully")
            );
        } catch (Exception e) {
            log.error("Failed to cancel order {}", orderId, e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Failed to cancel order: " + e.getMessage())
            );
        }
    }

    /**
     * Update order notes (admin)
     * PUT /api/admin/orders/{orderId}/notes
     */
    @PutMapping("/{orderId}/notes")
    public ResponseEntity<ApiResponse> updateOrderNotes(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request
    ) {
        try {
            String notes = request.get("notes");
            log.info("Admin updating notes for order ID: {}", orderId);
            orderService.updateOrderNotes(orderId, notes);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Order notes updated successfully")
            );
        } catch (Exception e) {
            log.error("Failed to update notes for order {}", orderId, e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Failed to update notes: " + e.getMessage())
            );
        }
    }

    /**
     * Refund order (admin)
     * POST /api/admin/orders/{orderId}/refund
     */
    @PostMapping("/{orderId}/refund")
    public ResponseEntity<ApiResponse> refundOrder(@PathVariable Long orderId) {
        try {
            log.info("Admin refunding order ID: {}", orderId);
            orderService.refundOrder(orderId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Order refunded successfully")
            );
        } catch (Exception e) {
            log.error("Failed to refund order {}", orderId, e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Failed to refund order: " + e.getMessage())
            );
        }
    }

    /**
     * Reassign staff to order
     * PUT /api/admin/orders/{orderId}/reassign
     */
    @PutMapping("/{orderId}/reassign")
    public ResponseEntity<ApiResponse> reassignStaff(
            @PathVariable Long orderId,
            @RequestBody Map<String, Long> request
    ) {
        try {
            Long chefId = request.get("chefId");
            Long waiterId = request.get("waiterId");

            log.info("Admin reassigning staff for order ID: {} - Chef: {}, Waiter: {}",
                    orderId, chefId, waiterId);

            orderService.reassignStaff(orderId, chefId, waiterId);

            return ResponseEntity.ok(
                    new ApiResponse(true, "Staff reassigned successfully")
            );
        } catch (Exception e) {
            log.error("Failed to reassign staff for order {}", orderId, e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Failed to reassign staff: " + e.getMessage())
            );
        }
    }

    /**
     * Get order statistics
     * GET /api/admin/orders/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        try {
            log.info("Admin fetching order statistics");
            Map<String, Object> stats = orderService.getOrderStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to fetch order statistics", e);
            throw new RuntimeException("Failed to fetch statistics: " + e.getMessage());
        }
    }
}