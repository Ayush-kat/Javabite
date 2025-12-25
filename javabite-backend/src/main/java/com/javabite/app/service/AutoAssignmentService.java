package com.javabite.app.service;

import com.javabite.app.model.*;
import com.javabite.app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoAssignmentService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ChefQueueRepository chefQueueRepository;
    private final WaiterQueueRepository waiterQueueRepository;
    private final OrderService orderService;

    /**
     * ✅ Runs every minute to check for orders pending > 3 minutes
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    @Transactional
    public void autoAssignPendingOrders() {
        try {
            log.debug("🔄 Checking for orders needing auto-assignment...");

            LocalDateTime threeMinutesAgo = LocalDateTime.now().minusMinutes(3);

            List<Order> eligibleOrders = orderRepository.findAll().stream()
                    .filter(o -> o.getStatus() == OrderStatus.PENDING)
                    .filter(o -> o.getChef() == null)
                    .filter(o -> o.getCreatedAt().isBefore(threeMinutesAgo))
                    .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                    .collect(Collectors.toList());

            if (eligibleOrders.isEmpty()) {
                return;
            }

            log.info("🔍 Found {} orders for auto-assignment", eligibleOrders.size());

            for (Order order : eligibleOrders) {
                tryAutoAssignChef(order);
            }
        } catch (Exception e) {
            log.error("❌ Error in auto-assignment: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void tryAutoAssignChef(Order order) {
        List<User> availableChefs = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CHEF)
                .filter(User::isEnabled)
                .filter(User::canAcceptOrder)
                .sorted((a, b) -> Integer.compare(
                        a.getCurrentActiveOrders(),
                        b.getCurrentActiveOrders()
                ))
                .collect(Collectors.toList());

        if (!availableChefs.isEmpty()) {
            User chef = availableChefs.get(0);
            try {
                orderService.assignChefToOrder(order.getId(), chef.getId());
                log.info("✅ Auto-assigned Order #{} to Chef {}", order.getId(), chef.getName());
            } catch (Exception e) {
                log.error("❌ Auto-assignment failed: {}", e.getMessage());
                addToChefQueueIfNotExists(order);
            }
        } else {
            addToChefQueueIfNotExists(order);
            log.info("📋 Order #{} queued (no available chefs)", order.getId());
        }
    }

    @Transactional
    public void onChefCapacityFreed(Long chefId) {
        List<ChefQueue> queue = chefQueueRepository.findAllByOrderByQueuePositionAsc();

        if (queue.isEmpty()) {
            return;
        }

        ChefQueue nextInQueue = queue.get(0);
        Order order = nextInQueue.getOrder();

        try {
            orderService.assignChefToOrder(order.getId(), chefId);
            chefQueueRepository.delete(nextInQueue);
            log.info("✅ Assigned queued Order #{} to Chef {}", order.getId(), chefId);
        } catch (Exception e) {
            log.error("❌ Failed to assign from queue: {}", e.getMessage());
        }
    }

    @Transactional
    public void onWaiterCapacityFreed(Long waiterId) {
        List<WaiterQueue> queue = waiterQueueRepository.findAllByOrderByQueuePositionAsc();

        if (queue.isEmpty()) {
            return;
        }

        WaiterQueue nextInQueue = queue.get(0);
        Order order = nextInQueue.getOrder();

        try {
            orderService.assignWaiterToOrder(order.getId(), waiterId);
            waiterQueueRepository.delete(nextInQueue);
            log.info("✅ Assigned queued Order #{} to Waiter {}", order.getId(), waiterId);
        } catch (Exception e) {
            log.error("❌ Failed to assign waiter from queue: {}", e.getMessage());
        }
    }

    private void addToChefQueueIfNotExists(Order order) {
        boolean exists = chefQueueRepository.findByOrderId(order.getId()).isPresent();

        if (!exists) {
            Integer maxPos = chefQueueRepository.findMaxQueuePosition();
            int nextPos = (maxPos == null) ? 1 : maxPos + 1;

            ChefQueue entry = ChefQueue.builder()
                    .order(order)
                    .queuePosition(nextPos)
                    .build();

            chefQueueRepository.save(entry);
        }
    }
}