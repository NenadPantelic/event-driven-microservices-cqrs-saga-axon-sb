package com.npdev.estore.payment_service.query.handler;

import com.npdev.estore.core.event.PaymentProcessedEvent;
import com.npdev.estore.payment_service.query.model.Payment;
import com.npdev.estore.payment_service.query.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
@ProcessingGroup("payment-group")
public class PaymentEventHandler {

    private final PaymentRepository paymentRepository;

    @EventHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        Payment payment = Payment.builder()
                .paymentId(paymentProcessedEvent.getPaymentId())
                .orderId(paymentProcessedEvent.getOrderId())
                .build();

        paymentRepository.save(payment);
    }

}
