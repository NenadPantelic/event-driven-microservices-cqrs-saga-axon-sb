package com.npdev.estore.core.event;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentProcessedEvent {

    private final String orderId;
    private final String paymentId;
}
