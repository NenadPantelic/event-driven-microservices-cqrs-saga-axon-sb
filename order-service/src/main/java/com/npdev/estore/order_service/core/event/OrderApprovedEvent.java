package com.npdev.estore.order_service.core.event;

import com.npdev.estore.order_service.command.dto.internal.OrderStatus;
import lombok.Value;

@Value // getter, all args constructor, toString and equals and hashcode; used for immutable objects
public class OrderApprovedEvent {
    String orderId;
    OrderStatus orderStatus;
}

