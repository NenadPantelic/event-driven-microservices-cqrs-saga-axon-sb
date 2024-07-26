package com.npdev.estore.payment_service.aggregate;

import com.npdev.estore.core.command.ProcessPaymentCommand;
import com.npdev.estore.core.event.PaymentProcessedEvent;
import io.micrometer.common.util.StringUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Slf4j
@Aggregate
@NoArgsConstructor
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        log.info("Handling process payment command: {}", processPaymentCommand);

        if (StringUtils.isBlank(processPaymentCommand.getPaymentId())) {
            throw new IllegalArgumentException("Payment id cannot be empty.");
        }

        if (StringUtils.isBlank(processPaymentCommand.getOrderId())) {
            throw new IllegalArgumentException("Payment order id cannot be empty.");
        }

        PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
                .orderId(processPaymentCommand.getOrderId())
                .paymentId(processPaymentCommand.getPaymentId())
                .build();

        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        paymentId = paymentProcessedEvent.getPaymentId();
        orderId = paymentProcessedEvent.getOrderId();
    }
}
