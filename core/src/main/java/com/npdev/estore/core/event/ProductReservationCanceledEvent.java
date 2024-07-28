package com.npdev.estore.core.event;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductReservationCanceledEvent {

    private final String productId;
    private final int quantity;
    private final String orderId;
    private final String userId;
    private final String reason;
}
