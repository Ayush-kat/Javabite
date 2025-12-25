package com.javabite.app.service;

import com.javabite.app.model.Category;
import com.javabite.app.model.MenuItem;
import com.javabite.app.payload.CreateMenuItemRequest;
import com.javabite.app.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }

    public List<MenuItem> getMenuItemsByCategory(Category category) {
        return menuItemRepository.findByCategoryAndAvailableTrue(category);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }

    @Transactional
    public MenuItem createMenuItem(CreateMenuItemRequest request) {
        MenuItem menuItem = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .available(true) // Always true by default
                .build();

        MenuItem saved = menuItemRepository.save(menuItem);
        log.info("Created menu item: {}", saved.getName());
        return saved;
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, CreateMenuItemRequest request) {
        MenuItem menuItem = getMenuItemById(id);

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setImageUrl(request.getImageUrl());
        // available field stays unchanged

        MenuItem updated = menuItemRepository.save(menuItem);
        log.info("Updated menu item: {}", updated.getName());
        return updated;
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem menuItem = getMenuItemById(id);
        menuItemRepository.delete(menuItem);
        log.info("Deleted menu item: {}", menuItem.getName());
    }

}
