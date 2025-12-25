package com.javabite.app.config;

import com.javabite.app.model.Role;
import com.javabite.app.model.User;
import com.javabite.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Order(1)
    public CommandLineRunner initializeData() {
        return args -> {
            // Check if users already exist
            if (userRepository.count() > 0) {
                log.info("Users already exist. Checking if passwords need updating...");

                // CRITICAL FIX: Update existing users with hashed passwords if needed
                updateExistingUsersPasswords();
                return;
            }

            log.info("Initializing test users with hashed passwords...");

            // AFTER (CORRECT):
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@javabite.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .maxActiveOrders(999)
                    .currentActiveOrders(0)
                    .isAvailable(true)
                    // ✅ createdAt auto-set by @PrePersist
                    .build();

            userRepository.save(admin);
            log.info("✅ Created admin user: admin@javabite.com / admin123");

            // Create Chef User
            User chef = User.builder()
                    .name("John Chef")
                    .email("chef@javabite.com")
                    .password(passwordEncoder.encode("chef123"))  // ✅ HASHED
                    .role(Role.CHEF)
                    .enabled(true)
                    .maxActiveOrders(10)  // Chef can handle 10 orders
                    .currentActiveOrders(0)
                    .isAvailable(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(chef);
            log.info("✅ Created chef user: chef@javabite.com / chef123");

            // Create Waiter User
            User waiter = User.builder()
                    .name("Sarah Waiter")
                    .email("waiter@javabite.com")
                    .password(passwordEncoder.encode("waiter123"))  //  HASHED
                    .role(Role.WAITER)
                    .enabled(true)
                    .maxActiveOrders(1)  // Waiter can serve 1 table at a time
                    .currentActiveOrders(0)
                    .isAvailable(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(waiter);
            log.info(" Created waiter user: waiter@javabite.com / waiter123");

            // Create Customer User
            User customer = User.builder()
                    .name("Jane Customer")
                    .email("customer@javabite.com")
                    .password(passwordEncoder.encode("customer123"))  //  HASHED
                    .role(Role.CUSTOMER)
                    .enabled(true)
                    .maxActiveOrders(5)  // Customers can have 5 active orders
                    .currentActiveOrders(0)
                    .isAvailable(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(customer);
            log.info(" Created customer user: customer@javabite.com / customer123");

            log.info("================================================");
            log.info("🎉 All test users created successfully!");
            log.info("================================================");
            log.info("Login Credentials:");
            log.info("Admin:    admin@javabite.com    / admin123");
            log.info("Chef:     chef@javabite.com     / chef123");
            log.info("Waiter:   waiter@javabite.com   / waiter123");
            log.info("Customer: customer@javabite.com / customer123");
            log.info("================================================");
        };
    }

    /**
     * CRITICAL FIX: Update existing users with hashed passwords
     * This handles the case where users were created with plaintext passwords
     */
    private void updateExistingUsersPasswords() {
        // Known test users and their plaintext passwords
        String[][] testUsers = {
                {"admin@javabite.com", "admin123"},
                {"chef@javabite.com", "chef123"},
                {"waiter@javabite.com", "waiter123"},
                {"customer@javabite.com", "customer123"}
        };

        for (String[] userInfo : testUsers) {
            String email = userInfo[0];
            String plainPassword = userInfo[1];

            userRepository.findByEmail(email).ifPresent(user -> {
                // Check if password is already hashed (BCrypt hashes start with $2a$ or $2b$)
                if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
                    log.warn("⚠️ User {} has unhashed password. Updating...", email);
                    user.setPassword(passwordEncoder.encode(plainPassword));

                    // Also ensure capacity fields are set
                    if (user.getMaxActiveOrders() == null) {
                        switch (user.getRole()) {
                            case ADMIN:
                                user.setMaxActiveOrders(999);
                                break;
                            case CHEF:
                                user.setMaxActiveOrders(10);
                                break;
                            case WAITER:
                                user.setMaxActiveOrders(1);
                                break;
                            case CUSTOMER:
                                user.setMaxActiveOrders(5);
                                break;
                        }
                    }

                    if (user.getCurrentActiveOrders() == null) {
                        user.setCurrentActiveOrders(0);
                    }

                    if (user.getIsAvailable() == null) {
                        user.setIsAvailable(true);
                    }

                    userRepository.save(user);
                    log.info("✅ Updated password and capacity fields for {}", email);
                }
            });
        }
    }
}