package com.javabite.app.scheduler;

import com.javabite.app.model.Order;
import com.javabite.app.model.OrderStatus;
import com.javabite.app.model.Role;
import com.javabite.app.model.User;
import com.javabite.app.repository.OrderRepository;
import com.javabite.app.repository.UserRepository;
import com.javabite.app.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoAssignmentScheduler {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    /**
     * ✅ Auto-assign orders that have been pending for more than 5 minutes
     * Runs every minute
     */
    @Scheduled(fixedRate = 60000) // Run every 1 minute
    @Transactional
    public void autoAssignPendingOrders() {
        try {
            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

            // ✅ Find PENDING orders without chef, older than 5 minutes
            List<Order> unassignedOrders = orderRepository
                    .findByStatusAndChefIsNull(OrderStatus.PENDING)
                    .stream()
                    .filter(o -> o.getCreatedAt().isBefore(fiveMinutesAgo))
                    .collect(Collectors.toList());

            if (unassignedOrders.isEmpty()) {
                return;
            }

            log.info("🤖 Auto-assignment: Found {} orders pending for 5+ minutes",
                    unassignedOrders.size());

            // ✅ Get available chefs (enabled + can accept orders)
            List<User> availableChefs = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.CHEF)
                    .filter(User::isEnabled)
                    .filter(User::canAcceptOrder)
                    .collect(Collectors.toList());

            // ✅ Get available waiters
            List<User> availableWaiters = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.WAITER)
                    .filter(User::isEnabled)
                    .filter(User::canAcceptOrder)
                    .collect(Collectors.toList());

            if (availableChefs.isEmpty()) {
                log.warn("⚠️ No available chefs for auto-assignment");
                // Add to queue instead
                for (Order order : unassignedOrders) {
                    User anyChef = userRepository.findAll().stream()
                            .filter(u -> u.getRole() == Role.CHEF && u.isEnabled())
                            .findFirst()
                            .orElse(null);

                    if (anyChef != null && !orderService.isOrderInChefQueue(order.getId())) {
                        orderService.addToChefQueue(order, anyChef);
                        log.info("⏳ Added Order #{} to Chef queue (auto-assignment)", order.getId());
                    }
                }
                return;
            }

            // ✅ Auto-assign orders
            int assignedCount = 0;
            int chefIndex = 0;
            int waiterIndex = 0;

            for (Order order : unassignedOrders) {
                if (availableChefs.isEmpty()) {
                    log.warn("⚠️ No more available chefs");
                    break;
                }

                // Get next available chef (round-robin)
                User chef = availableChefs.get(chefIndex % availableChefs.size());

                // Assign chef to order
                order.setChef(chef);
                order.setChefAssignedAt(LocalDateTime.now());
                order.setAutoAssigned(true); // ✅ Mark as auto-assigned

                // Optionally assign waiter if available
                if (!availableWaiters.isEmpty()) {
                    User waiter = availableWaiters.get(waiterIndex % availableWaiters.size());
                    order.setWaiter(waiter);
                    order.setWaiterAssignedAt(LocalDateTime.now());

                    waiter.incrementActiveOrders();
                    userRepository.save(waiter);

                    // Move to next waiter
                    waiterIndex++;
                    if (!waiter.canAcceptOrder()) {
                        availableWaiters.remove(waiter);
                        waiterIndex = 0;
                    }
                }

                chef.incrementActiveOrders();
                userRepository.save(chef);
                orderRepository.save(order);

                assignedCount++;

                log.info("🤖 Auto-assigned Order #{} → Chef: {}{}",
                        order.getId(),
                        chef.getName(),
                        order.getWaiter() != null ? ", Waiter: " + order.getWaiter().getName() : "");

                // Move to next chef
                chefIndex++;
                if (!chef.canAcceptOrder()) {
                    availableChefs.remove(chef);
                    chefIndex = 0;
                }
            }

            if (assignedCount > 0) {
                log.info("✅ Auto-assignment completed: {} orders assigned", assignedCount);
            }

        } catch (Exception e) {
            log.error("❌ Auto-assignment failed", e);
        }
    }

    /**
     * ✅ Process queued orders when staff becomes available
     * Runs every 2 minutes
     */
    @Scheduled(fixedRate = 120000) // Run every 2 minutes
    @Transactional
    public void processQueuedOrders() {
        try {
            // TODO: Implement queue processing
            // Get orders from chef_queue and waiter_queue
            // Assign to available staff
            // Remove from queue
            log.debug("🔄 Queue processing - checking for available staff...");
        } catch (Exception e) {
            log.error("❌ Queue processing failed", e);
        }
    }
}