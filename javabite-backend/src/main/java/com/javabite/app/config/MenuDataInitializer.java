package com.javabite.app.config;

import com.javabite.app.model.Category;
import com.javabite.app.model.MenuItem;
import com.javabite.app.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MenuDataInitializer {
    private final MenuItemRepository menuItemRepository;

    @Bean
    @Order(2) // Run after user initialization
    public CommandLineRunner initializeMenuData() {
        return args -> {
            if (menuItemRepository.count() > 0) {
                log.info("Menu items already exist in database. Skipping initialization.");
                return;
            }

            log.info("Initializing menu items...");

            // Coffee Items
            createMenuItem("Espresso", "Rich and bold shot of pure coffee",
                    new BigDecimal("2.50"), Category.COFFEE,
                    "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?w=400&h=400&fit=crop");

            createMenuItem("Cappuccino", "Perfect balance of espresso, steamed milk, and foam",
                    new BigDecimal("3.50"), Category.COFFEE,
                    "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400&h=400&fit=crop");

            createMenuItem("Latte", "Smooth espresso with steamed milk and light foam",
                    new BigDecimal("4.00"), Category.COFFEE,
                    "https://images.unsplash.com/photo-1561882468-9110e03e0f78?w=400&h=400&fit=crop");

            createMenuItem("Americano", "Espresso diluted with hot water for a lighter taste",
                    new BigDecimal("3.00"), Category.COFFEE,
                    "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400&h=400&fit=crop");

            createMenuItem("Cold Brew", "Smooth, refreshing cold steeped coffee",
                    new BigDecimal("3.75"), Category.COFFEE,
                    "https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?w=400&h=400&fit=crop");

            // Pastries
            createMenuItem("Croissant", "Buttery and flaky French pastry",
                    new BigDecimal("3.00"), Category.PASTRIES,
                    "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400&h=400&fit=crop");

            createMenuItem("Blueberry Muffin", "Fresh baked with real blueberries",
                    new BigDecimal("2.50"), Category.PASTRIES,
                    "https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=400&h=400&fit=crop");

            createMenuItem("Chocolate Chip Cookie", "Warm and gooey with premium chocolate",
                    new BigDecimal("2.00"), Category.PASTRIES,
                    "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&h=400&fit=crop");

            createMenuItem("Cinnamon Roll", "Soft roll with cream cheese frosting",
                    new BigDecimal("3.50"), Category.PASTRIES,
                    "https://images.unsplash.com/photo-1626094309830-abbb0c99da4a?w=400&h=400&fit=crop");

            log.info("Menu items initialized successfully!");
        };
    }

    private void createMenuItem(String name, String description, BigDecimal price,
                                Category category, String imageUrl) {
        MenuItem item = MenuItem.builder()
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .imageUrl(imageUrl)
                .available(true)
                .build();

        menuItemRepository.save(item);
        log.info("Created menu item: {}", name);
    }
}