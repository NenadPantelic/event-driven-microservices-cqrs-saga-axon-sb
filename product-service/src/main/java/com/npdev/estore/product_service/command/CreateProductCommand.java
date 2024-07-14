package com.npdev.estore.product_service.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

// <verb><noun>Command
@Builder
@Data
public class CreateProductCommand {

    @TargetAggregateIdentifier // associates this command with the aggregate object
    private final String productId;
    private final String title;
    private final BigDecimal price;
    private final Integer quantity;
}
