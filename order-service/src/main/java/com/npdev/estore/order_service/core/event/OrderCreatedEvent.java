package com.npdev.estore.order_service.core.event;

import com.npdev.estore.order_service.command.dto.internal.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreatedEvent {

    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
