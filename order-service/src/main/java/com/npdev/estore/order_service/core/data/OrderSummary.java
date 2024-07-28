package com.npdev.estore.order_service.core.data;

import com.npdev.estore.order_service.command.dto.internal.OrderStatus;
import lombok.Value;

@Value
public class OrderSummary {

    String orderId;
    OrderStatus orderStatus;
    String message;
}
