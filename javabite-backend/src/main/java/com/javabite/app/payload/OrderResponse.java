package com.javabite.app.payload;

import com.javabite.app.model.Order;
import com.javabite.app.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long chefId;
    private String chefName;
    private Long waiterId;  // NEW
    private String waiterName;  // NEW
    private Integer tableNumber;  // NEW
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private String specialInstructions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readyAt;  // NEW
    private LocalDateTime servedAt;  // NEW
    private LocalDateTime completedAt;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .chefId(order.getChef() != null ? order.getChef().getId() : null)
                .chefName(order.getChef() != null ? order.getChef().getName() : null)
                .waiterId(order.getWaiter() != null ? order.getWaiter().getId() : null)
                .waiterName(order.getWaiter() != null ? order.getWaiter().getName() : null)
                .tableNumber(order.getTableNumber())
                .items(order.getItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .collect(Collectors.toList()))
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .specialInstructions(order.getSpecialInstructions())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .readyAt(order.getReadyAt())
                .servedAt(order.getServedAt())
                .completedAt(order.getCompletedAt())
                .build();
    }
}