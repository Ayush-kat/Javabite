package com.javabite.app.repository;

import com.javabite.app.model.ChefQueue;
import com.javabite.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChefQueueRepository extends JpaRepository<ChefQueue, Long> {

    // ✅ Required methods
    Optional<ChefQueue> findByOrderId(Long orderId);
    Long countByChef(User chef);
    List<ChefQueue> findAllByOrderByQueuePositionAsc();
    List<ChefQueue> findByChefOrderByQueuePositionAsc(User chef);

    @Query("SELECT MAX(c.queuePosition) FROM ChefQueue c")
    Integer findMaxQueuePosition();
}