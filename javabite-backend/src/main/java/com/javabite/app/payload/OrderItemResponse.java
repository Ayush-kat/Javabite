package com.javabite.app.payload;

import com.javabite.app.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private MenuItemResponse menuItem;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private String notes;

    public static OrderItemResponse fromEntity(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItem(MenuItemResponse.fromEntity(item.getMenuItem()))
                .quantity(item.getQuantity())
                .priceAtOrder(BigDecimal.valueOf(item.getPriceAtOrder()))
                .notes(item.getNotes())
                .build();
    }
}
