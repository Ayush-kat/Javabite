package com.javabite.app.controller;

import com.javabite.app.model.Order;
import com.javabite.app.model.User;
import com.javabite.app.payload.*;
import com.javabite.app.service.OrderService;
import com.javabite.app.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/create-chef")
    public ResponseEntity<ApiResponse> createChef(@Valid @RequestBody CreateChefRequest request) {
        try {
            log.info("🔵 Creating chef: {}", request.getEmail());
            User chef = userService.createChef(request);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", chef.getId());
            responseData.put("name", chef.getName());
            responseData.put("email", chef.getEmail());
            responseData.put("role", chef.getRole());
            responseData.put("temporaryPassword", chef.getPassword());

            log.info("✅ Chef created successfully: {}", chef.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Chef created successfully", responseData));
        } catch (RuntimeException e) {
            log.error("❌ Failed to create chef: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/create-waiter")
    public ResponseEntity<ApiResponse> createWaiter(@Valid @RequestBody CreateWaiterRequest request) {
        try {
            log.info("🔵 Creating waiter: {}", request.getEmail());
            User waiter = userService.createWaiter(request);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", waiter.getId());
            responseData.put("name", waiter.getName());
            responseData.put("email", waiter.getEmail());
            responseData.put("role", waiter.getRole());
            responseData.put("temporaryPassword", waiter.getPassword());

            log.info("✅ Waiter created successfully: {}", waiter.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Waiter created successfully", responseData));
        } catch (RuntimeException e) {
            log.error("❌ Failed to create waiter: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/staff/chefs")
    public ResponseEntity<ApiResponse> getAllChefs() {
        log.info("🔵 Fetching all chefs");
        List<User> chefs = userService.getAllChefs();

        List<Map<String, Object>> response = chefs.stream()
                .map(chef -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", chef.getId());
                    data.put("name", chef.getName());
                    data.put("email", chef.getEmail());
                    data.put("enabled", chef.isEnabled());
                    return data;
                })
                .collect(Collectors.toList());

        log.info("✅ Retrieved {} chefs", response.size());
        return ResponseEntity.ok(new ApiResponse(true, "Chefs retrieved successfully", response));
    }

    @GetMapping("/staff/waiters")
    public ResponseEntity<ApiResponse> getAllWaiters() {
        log.info("🔵 Fetching all waiters");
        List<User> waiters = userService.getAllWaiters();

        List<Map<String, Object>> response = waiters.stream()
                .map(waiter -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", waiter.getId());
                    data.put("name", waiter.getName());
                    data.put("email", waiter.getEmail());
                    data.put("enabled", waiter.isEnabled());
                    return data;
                })
                .collect(Collectors.toList());

        log.info("✅ Retrieved {} waiters", response.size());
        return ResponseEntity.ok(new ApiResponse(true, "Waiters retrieved successfully", response));
    }

    @PutMapping("/staff/{userId}/toggle")
    public ResponseEntity<ApiResponse> toggleStaffStatus(@PathVariable Long userId) {
        try {
            log.info("🔵 Toggling staff status for user ID: {}", userId);
            User user = userService.toggleUserStatus(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("name", user.getName());
            data.put("enabled", user.isEnabled());

            log.info("✅ Staff status toggled: {} - {}", user.getName(), user.isEnabled() ? "ENABLED" : "DISABLED");
            return ResponseEntity.ok(new ApiResponse(true, "Staff status updated", data));
        } catch (RuntimeException e) {
            log.error("❌ Failed to toggle staff status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/staff/{userId}")
    public ResponseEntity<ApiResponse> deleteStaff(@PathVariable Long userId) {
        try {
            log.info("🔵 Deleting staff with ID: {}", userId);
            userService.deleteUser(userId);

            log.info("✅ Staff member deleted successfully");
            return ResponseEntity.ok(new ApiResponse(true, "Staff member deleted successfully"));
        } catch (RuntimeException e) {
            log.error("❌ Failed to delete staff: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/orders/pending")
    public ResponseEntity<ApiResponse> getPendingOrders() {
        log.info("🔵 Fetching pending orders");
        List<Order> orders = orderService.getPendingOrders();

        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());

        log.info("✅ Retrieved {} pending orders", response.size());
        return ResponseEntity.ok(new ApiResponse(true, "Pending orders retrieved", response));
    }

    /**
     * ✅ CRITICAL FIX: Combined assignment endpoint with enhanced debugging
     */
    @PostMapping("/orders/{orderId}/assign")
    public ResponseEntity<ApiResponse> assignStaff(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> request) {

        log.info("🔵 ========================================");
        log.info("🔵 ASSIGN STAFF REQUEST RECEIVED");
        log.info("🔵 Order ID: {}", orderId);
        log.info("🔵 Request Body: {}", request);
        log.info("🔵 ========================================");

        try {
            // Parse IDs with detailed logging
            Long chefId = null;
            Long waiterId = null;

            if (request.get("chefId") != null) {
                Object chefIdObj = request.get("chefId");
                log.info("🔵 Raw chefId value: {} (type: {})", chefIdObj, chefIdObj.getClass().getName());
                chefId = Long.valueOf(chefIdObj.toString());
                log.info("🔵 Parsed chefId: {}", chefId);
            }

            if (request.get("waiterId") != null) {
                Object waiterIdObj = request.get("waiterId");
                log.info("🔵 Raw waiterId value: {} (type: {})", waiterIdObj, waiterIdObj.getClass().getName());
                waiterId = Long.valueOf(waiterIdObj.toString());
                log.info("🔵 Parsed waiterId: {}", waiterId);
            }

            // Validation
            if (chefId == null) {
                log.error("❌ Chef ID is missing");
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Chef ID is required"));
            }

            log.info("🔵 Assigning Order #{} to Chef ID: {}, Waiter ID: {}", orderId, chefId, waiterId);

            // Step 1: Assign chef
            log.info("🔵 Step 1: Assigning chef...");
            Order order = orderService.assignChefToOrder(orderId, chefId);
            log.info("✅ Chef assigned successfully. Order status: {}", order.getStatus());

            // Step 2: Assign waiter if provided
            if (waiterId != null) {
                log.info("🔵 Step 2: Assigning waiter...");
                order = orderService.assignWaiterToOrder(orderId, waiterId);
                log.info("✅ Waiter assigned successfully. Order status: {}", order.getStatus());
            } else {
                log.info("ℹ️ No waiter assigned (optional)");
            }

            log.info("✅ ========================================");
            log.info("✅ ORDER ASSIGNMENT COMPLETED SUCCESSFULLY");
            log.info("✅ Order #{}: Chef ID {}, Waiter ID {}", orderId, chefId, waiterId);
            log.info("✅ ========================================");

            return ResponseEntity.ok(
                    new ApiResponse(true, "Staff assigned successfully", OrderResponse.fromEntity(order))
            );

        } catch (RuntimeException e) {
            log.error("❌ ========================================");
            log.error("❌ ASSIGNMENT FAILED");
            log.error("❌ Order ID: {}", orderId);
            log.error("❌ Error: {}", e.getMessage());
            log.error("❌ Exception type: {}", e.getClass().getName());
            log.error("❌ Stack trace:", e);
            log.error("❌ ========================================");

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/assign-chef")
    public ResponseEntity<ApiResponse> assignChef(
            @PathVariable Long orderId,
            @RequestBody Map<String, Long> request) {
        try {
            Long chefId = request.get("chefId");
            if (chefId == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Chef ID is required"));
            }

            log.info("🔵 Assigning chef {} to order {}", chefId, orderId);
            Order order = orderService.assignChefToOrder(orderId, chefId);

            log.info("✅ Chef assigned successfully");
            return ResponseEntity.ok(
                    new ApiResponse(true, "Chef assigned successfully", OrderResponse.fromEntity(order))
            );
        } catch (RuntimeException e) {
            log.error("❌ Failed to assign chef: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/assign-waiter")
    public ResponseEntity<ApiResponse> assignWaiter(
            @PathVariable Long orderId,
            @RequestBody Map<String, Long> request) {
        try {
            Long waiterId = request.get("waiterId");
            if (waiterId == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Waiter ID is required"));
            }

            log.info("🔵 Assigning waiter {} to order {}", waiterId, orderId);
            Order order = orderService.assignWaiterToOrder(orderId, waiterId);

            log.info("✅ Waiter assigned successfully");
            return ResponseEntity.ok(
                    new ApiResponse(true, "Waiter assigned successfully", OrderResponse.fromEntity(order))
            );
        } catch (RuntimeException e) {
            log.error("❌ Failed to assign waiter: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        try {
            log.info("🔵 Fetching dashboard stats");
            Map<String, Object> stats = orderService.getAdminDashboardStats();

            log.info("✅ Dashboard stats retrieved");
            return ResponseEntity.ok(new ApiResponse(true, "Dashboard stats retrieved", stats));
        } catch (RuntimeException e) {
            log.error("❌ Failed to get dashboard stats: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
    @PostConstruct
    public void init() {
        log.info("✅ AdminController LOADED - /api/admin endpoints active");
        log.info("✅ Available endpoint: POST /api/admin/orders/{orderId}/assign");
    }
}