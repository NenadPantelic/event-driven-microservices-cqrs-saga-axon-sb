package com.npdev.estore.order_service.saga;

import com.npdev.estore.core.command.CancelProductReservationCommand;
import com.npdev.estore.core.command.ProcessPaymentCommand;
import com.npdev.estore.core.command.ReserveProductCommand;
import com.npdev.estore.core.event.PaymentProcessedEvent;
import com.npdev.estore.core.event.ProductReservationCanceledEvent;
import com.npdev.estore.core.event.ProductReservedEvent;
import com.npdev.estore.core.model.User;
import com.npdev.estore.core.query.FetchUserPaymentDetailsQuery;
import com.npdev.estore.order_service.command.ApproveOrderCommand;
import com.npdev.estore.order_service.command.RejectOrderCommand;
import com.npdev.estore.order_service.core.data.OrderSummary;
import com.npdev.estore.order_service.core.event.OrderApprovedEvent;
import com.npdev.estore.order_service.core.event.OrderCreatedEvent;
import com.npdev.estore.order_service.core.event.OrderRejectedEvent;
import com.npdev.estore.order_service.query.FindOrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Saga
public class OrderSaga {

    private static final String PAYMENT_PROCESSING_DEADLINE = "payment-processing-deadline";

    @Autowired
    private transient CommandGateway commandGateway; // since Saga is serializable, we're making this transient
    // to avoid its serialization

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    // informs subscription queries about updates, errors and where there are no more updates
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private String scheduleId;

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
                rejectOrder(
                        orderCreatedEvent.getOrderId(),
                        String.format("Failed to reserve a product. Reason: %s", commandResultMessage.exceptionResult().getMessage())
                );
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        log.info("Handling ProductReservedEvent {}...", productReservedEvent);
        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = FetchUserPaymentDetailsQuery.builder()
                .userId(productReservedEvent.getUserId())
                .build();

        User user;
        try {
            user = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
            if (user == null) {
                cancelProductReservation(productReservedEvent, "Unable to fetch the user details");
                return;
            }
            log.info("Successfully fetched the user[{}] payment details.", user.getUserId());
        } catch (Exception e) {
            log.error("Failed to fetch the payment details: {}", e.getMessage(), e);
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }

        // set a deadline - wait for 10 seconds
        scheduleId = deadlineManager.schedule(
                Duration.of(10, ChronoUnit.SECONDS),
                PAYMENT_PROCESSING_DEADLINE,
                productReservedEvent
        );

        // to test deadline
        // if (true) return;
        // if the deadline is triggered, then a deadline handler is triggered
        // it should be marked with the deadline name to handle that exact deadline

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
            // result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            result = commandGateway.sendAndWait(processPaymentCommand);
            if (result == null) {
                log.info("The process payment command resulted with null. Initiating a compensating transaction...");
                cancelProductReservation(
                        productReservedEvent,
                        "Could not process user payment with provided details"
                );
            }
        } catch (Exception e) {
            log.error("Failed to send a process payment command: {}", e.getMessage(), e);
            cancelProductReservation(productReservedEvent, e.getMessage());
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String errorMessage) {
        cancelDeadline();
        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .userId(productReservedEvent.getUserId())
                .quantity(productReservedEvent.getQuantity())
                .reason(errorMessage)
                .build();

        commandGateway.sendAndWait(cancelProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        log.info("Handling PaymentProcessedEvent: {}", paymentProcessedEvent);
        cancelDeadline();
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.sendAndWait(approveOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCanceledEvent productReservationCanceledEvent) {
        log.info("Handling ProductReservationCanceledEvent: {}", productReservationCanceledEvent);
        rejectOrder(productReservationCanceledEvent.getOrderId(), productReservationCanceledEvent.getReason());
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        log.info("Order is approved. Saga is complete for orderId: {}", orderApprovedEvent.getOrderId());
        // SagaLifecycle.end(); ends saga (the same as using @EndSaga, but more flexible
        queryUpdateEmitter.emit(
                FindOrderQuery.class,
                query -> true, // used to filter objects
                new OrderSummary(
                        orderApprovedEvent.getOrderId(),
                        orderApprovedEvent.getOrderStatus(),
                        ""
                )
        );
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        log.info("Order is rejected. Saga is completed for orderId: {}", orderRejectedEvent.getOrderId());
        queryUpdateEmitter.emit(
                FindOrderQuery.class,
                query -> true, // used to filter objects
                new OrderSummary(
                        orderRejectedEvent.getOrderId(),
                        orderRejectedEvent.getOrderStatus(),
                        orderRejectedEvent.getReason()
                )
        );
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        log.info("Payment processing deadline took place. Sending a compensating command to cancel it.");
        cancelProductReservation(productReservedEvent, "Payment processing took to long.");
    }

    private void cancelDeadline() {
        if (scheduleId != null) {
            // cancel all deadlines with this name
            // deadlineManager.cancelAll(PAYMENT_PROCESSING_DEADLINE);
            // cancel a deadline with a particular id
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }

    private void rejectOrder(String orderId, String reason) {
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderId, reason);
        commandGateway.sendAndWait(rejectOrderCommand);
    }

}
