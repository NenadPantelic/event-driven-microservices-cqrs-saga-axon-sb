package com.npdev.estore.order_service.saga;

import com.npdev.estore.core.command.ProcessPaymentCommand;
import com.npdev.estore.core.command.ReserveProductCommand;
import com.npdev.estore.core.event.PaymentProcessedEvent;
import com.npdev.estore.core.event.ProductReservedEvent;
import com.npdev.estore.core.model.User;
import com.npdev.estore.core.query.FetchUserPaymentDetailsQuery;
import com.npdev.estore.order_service.command.ApproveOrderCommand;
import com.npdev.estore.order_service.core.event.OrderApprovedEvent;
import com.npdev.estore.order_service.core.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway; // since Saga is serializable, we're making this transient
    // to avoid its serialization

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga // every saga has to have a starting method and an ending method
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        log.info("Handling OrderCreatedEvent {}...", orderCreatedEvent);
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // start a compensating transaction
            }
        });
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        log.info("Handling ProductReservedEvent {}...", productReservedEvent);
        // process user payment

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = FetchUserPaymentDetailsQuery.builder()
                .userId(productReservedEvent.getUserId())
                .build();

        User user;
        try {
            user = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
            if (user == null) {
                // start a compensating transaction
                return;
            }
            log.info("Successfully fetched the user[{}] payment details.", user.getUserId());
        } catch (Exception e) {
            log.error("Failed to fetch the payment details: {}", e.getMessage(), e);
            // start a compensating transaction
            return;
        }

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(user.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result;
        try {
            // wait for it to execute, it blocks until:
            // 1. the result is available
            // 2. timeout is reached - returns null
            // 3. a thread is interrupted - returns null
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            if (result == null) {
                log.info("The process payment command resulted with null. Initiating a compensating transaction...");
                // start a compensating transaction
            }
        } catch (Exception e) {
            log.error("Failed to send a process payment command: {}", e.getMessage(), e);

        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        log.info("Handling PaymentProcessedEvent: {}", paymentProcessedEvent);
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.sendAndWait(approveOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        log.info("Order is approved. Saga is complete for orderId: {}", orderApprovedEvent.getOrderId());
        // SagaLifecycle.end(); ends saga (the same as using @EndSaga, but more flexible
    }
}
