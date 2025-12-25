package com.javabite.app.controller;

import com.javabite.app.model.Category;
import com.javabite.app.model.MenuItem;
import com.javabite.app.payload.ApiResponse;
import com.javabite.app.payload.MenuItemResponse;
import com.javabite.app.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllMenuItems() {
        List<MenuItem> items = menuService.getAvailableMenuItems();
        List<MenuItemResponse> response = items.stream()
                .map(MenuItemResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new ApiResponse(true, "Menu items retrieved successfully", response)
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getMenuItemsByCategory(@PathVariable String category) {
        try {
            Category cat = Category.valueOf(category.toUpperCase());
            List<MenuItem> items = menuService.getMenuItemsByCategory(cat);
            List<MenuItemResponse> response = items.stream()
                    .map(MenuItemResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse(true, "Menu items retrieved successfully", response)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Invalid category"));
        }
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
}
