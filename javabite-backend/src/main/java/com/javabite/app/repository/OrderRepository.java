package com.javabite.app.repository;

import com.javabite.app.model.Order;
import com.javabite.app.model.OrderStatus;
import com.javabite.app.model.TableBooking;
import com.javabite.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Customer methods
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    // Admin methods
    List<Order> findByStatusAndChefIsNull(OrderStatus status);

    // Table booking methods
    List<Order> findByTableBooking(TableBooking tableBooking);

    // Chef methods
    List<Order> findByChefAndStatus(User chef, OrderStatus status);
    List<Order> findByChefAndStatusAndCompletedAtBetween(
            User chef, OrderStatus status, LocalDateTime start, LocalDateTime end);

    // Waiter methods
    List<Order> findByWaiterAndStatus(User waiter, OrderStatus status);
    List<Order> findByWaiter(User waiter);

    // Count methods for dashboard
    Long countByStatus(OrderStatus status);
    Long countByStatusAndCompletedAtAfter(OrderStatus status, LocalDateTime after);
}