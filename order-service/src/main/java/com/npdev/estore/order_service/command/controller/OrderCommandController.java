package com.npdev.estore.order_service.command.controller;

import com.npdev.estore.order_service.command.dto.NewOrder;
import com.npdev.estore.order_service.command.CreateOrderCommand;
import com.npdev.estore.order_service.core.data.OrderSummary;
import com.npdev.estore.order_service.query.FindOrderQuery;
import com.npdev.estore.order_service.query.model.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderCommandController {

    private static final String USER_ID = "00000000-0000-0000-0000-000000000000";

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public OrderSummary createOrder(@Valid @RequestBody NewOrder newOrder) {
        log.info("Create order: {}", newOrder);
        String orderId = UUID.randomUUID().toString();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .productId(newOrder.productId())
                .userId(USER_ID)
                .orderId(orderId)
                .addressId(newOrder.addressId())
                .quantity(newOrder.quantity())
                .build();


        // the initial result type, the one that comes later
        try (SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = queryGateway.subscriptionQuery(
                new FindOrderQuery(orderId),
                ResponseTypes.instanceOf(OrderSummary.class),
                ResponseTypes.instanceOf(OrderSummary.class)
        )) {
            commandGateway.sendAndWait(createOrderCommand);
            return queryResult.updates().blockFirst();
        }
    }

}
