package com.npdev.estore.order_service.core.event;

import com.npdev.estore.order_service.command.dto.internal.OrderStatus;
import lombok.Value;

@Value
public class OrderRejectedEvent {

    String orderId;
    String reason;
    OrderStatus orderStatus = OrderStatus.REJECTED;
}
