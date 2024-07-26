package com.npdev.estore.order_service.core.aggregate;

import com.npdev.estore.order_service.command.ApproveOrderCommand;
import com.npdev.estore.order_service.command.CreateOrderCommand;
import com.npdev.estore.order_service.command.dto.internal.OrderStatus;
import com.npdev.estore.order_service.core.event.OrderApprovedEvent;
import com.npdev.estore.order_service.core.event.OrderCreatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Slf4j
@Aggregate
@NoArgsConstructor
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    @CommandHandler
    // when the CreateProductCommand is dispatched, this constructor will be used to create an aggregate
    public OrderAggregate(CreateOrderCommand createOrderCommand) {

        log.info("Handling create order command: {}", createOrderCommand);

        // 1. validate
        if (StringUtils.isBlank(createOrderCommand.getOrderId())) {
            throw new IllegalArgumentException("Order id cannot be empty.");
        }

        if (StringUtils.isBlank(createOrderCommand.getProductId())) {
            throw new IllegalArgumentException("Product id cannot be empty.");
        }

        if (StringUtils.isBlank(createOrderCommand.getAddressId())) {
            throw new IllegalArgumentException("Address id cannot be empty.");
        }

        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(createOrderCommand.getOrderId())
                .productId(createOrderCommand.getProductId())
                .addressId(createOrderCommand.getAddressId())
                .userId(createOrderCommand.getUserId())
                .orderStatus(OrderStatus.CREATED)
                .quantity(createOrderCommand.getQuantity())
                .build();
        // copy properties with the same name from source to dest object
        // BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        // dispatch the event that will trigger the EventSourcingHandler
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        // initialize the latest state of the aggregate with the new event
        // when the exception happens, an event sourcing handler is not called
        // Axon does not immediately trigger this handler, but it stages it in case some error pops out
        productId = orderCreatedEvent.getProductId();
        userId = orderCreatedEvent.getUserId();
        orderId = orderCreatedEvent.getOrderId();
        quantity = orderCreatedEvent.getQuantity();
        addressId = orderCreatedEvent.getAddressId();
        orderStatus = orderCreatedEvent.getOrderStatus();
    }

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        log.info("Handling ApproveOrderCommand: {}", approveOrderCommand);
        // Create and publish the OrderApprovedEvent
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(
                approveOrderCommand.getOrderId(),
                OrderStatus.APPROVED
        );

        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        orderId = orderApprovedEvent.getOrderId();
        orderStatus = orderApprovedEvent.getOrderStatus();
    }
}
