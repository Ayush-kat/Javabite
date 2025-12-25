package com.javabite.app.controller;

import com.javabite.app.model.MenuItem;
import com.javabite.app.payload.ApiResponse;
import com.javabite.app.payload.CreateMenuItemRequest;
import com.javabite.app.payload.MenuItemResponse;
import com.javabite.app.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/menu")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminMenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllMenuItems() {
        List<MenuItem> items = menuService.getAllMenuItems();
        List<MenuItemResponse> response = items.stream()
                .map(MenuItemResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                new ApiResponse(true, "Menu items retrieved successfully", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMenuItemById(@PathVariable Long id) {
        try {
            MenuItem item = menuService.getMenuItemById(id);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Menu item retrieved successfully",
                            MenuItemResponse.fromEntity(item))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createMenuItem(
            @Valid @RequestBody CreateMenuItemRequest request) {
        try {
            MenuItem item = menuService.createMenuItem(request);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Menu item created successfully",
                            MenuItemResponse.fromEntity(item))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody CreateMenuItemRequest request) {
        try {
            MenuItem item = menuService.updateMenuItem(id, request);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Menu item updated successfully",
                            MenuItemResponse.fromEntity(item))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMenuItem(@PathVariable Long id) {
        try {
            menuService.deleteMenuItem(id);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Menu item deleted successfully")
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}