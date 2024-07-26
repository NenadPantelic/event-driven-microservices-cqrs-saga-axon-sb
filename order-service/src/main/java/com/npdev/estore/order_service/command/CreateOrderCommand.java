package com.npdev.estore.order_service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;

}
