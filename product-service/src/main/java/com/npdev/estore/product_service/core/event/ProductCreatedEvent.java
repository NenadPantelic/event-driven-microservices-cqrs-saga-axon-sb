package com.npdev.estore.product_service.core.event;

import lombok.Data;

import java.math.BigDecimal;

// <noun><performed-action>Event
@Data
public class ProductCreatedEvent {

    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
