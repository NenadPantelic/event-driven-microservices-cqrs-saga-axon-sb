package com.npdev.estore.order_service.query.handler;

import com.npdev.estore.order_service.core.event.OrderCreatedEvent;
import com.npdev.estore.order_service.query.model.Order;
import com.npdev.estore.order_service.query.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {

    private final OrderRepository orderRepository;

    @ExceptionHandler
    public void handle(Exception e) throws Exception {
        log.error("An Exception occurred: {}", e.getMessage(), e);
        throw e;
    }

    @ExceptionHandler // pay attention, this is from Axon, not Spring
    // it only handles the exceptions from the same event handling class
    public void handle(IllegalArgumentException e) {
        log.error("An IllegalArgumentException occurred: {}", e.getMessage(), e);
        throw e;
    }

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        Order order = Order.builder()
                .orderId(orderCreatedEvent.orderId())
                .productId(orderCreatedEvent.productId())
                .userId(orderCreatedEvent.userId())
                .quantity(orderCreatedEvent.quantity())
                .addressId(orderCreatedEvent.addressId())
                .orderStatus(orderCreatedEvent.orderStatus())
                .build();

        orderRepository.save(order);
    }
}
