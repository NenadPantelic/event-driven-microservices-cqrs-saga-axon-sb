package com.npdev.estore.order_service.core.event;

import com.npdev.estore.order_service.command.dto.internal.OrderStatus;
import lombok.Builder;

@Builder
public record OrderCreatedEvent(String orderId,
                                String productId,
                                String userId,
                                int quantity,
                                String addressId,
                                OrderStatus orderStatus) {
}
