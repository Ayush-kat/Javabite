package com.javabite.app.repository;

import com.javabite.app.model.WaiterQueue;
import com.javabite.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaiterQueueRepository extends JpaRepository<WaiterQueue, Long> {

    // ✅ Required methods
    Optional<WaiterQueue> findByOrderId(Long orderId);
    Long countByWaiter(User waiter);
    List<WaiterQueue> findAllByOrderByQueuePositionAsc();
    List<WaiterQueue> findByWaiterOrderByQueuePositionAsc(User waiter);

    @Query("SELECT MAX(w.queuePosition) FROM WaiterQueue w")
    Integer findMaxQueuePosition();
}