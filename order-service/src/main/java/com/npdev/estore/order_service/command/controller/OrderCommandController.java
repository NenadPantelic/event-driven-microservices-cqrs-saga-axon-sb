package com.npdev.estore.order_service.command.controller;

import com.npdev.estore.order_service.command.dto.NewOrder;
import com.npdev.estore.order_service.command.CreateOrderCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
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

    @PostMapping
    public String createOrder(@Valid @RequestBody NewOrder newOrder) {
        log.info("Create order: {}", newOrder);

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .productId(newOrder.productId())
                .userId(USER_ID)
                .orderId(UUID.randomUUID().toString())
                .addressId(newOrder.addressId())
                .quantity(newOrder.quantity())
                .build();


        String returnValue = commandGateway.sendAndWait(createOrderCommand);
        return String.format("POST createOrder: %s", returnValue);
    }

}
