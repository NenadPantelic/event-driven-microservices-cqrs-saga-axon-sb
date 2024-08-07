package com.npdev.estore.product_service.core.aggregate;

import com.npdev.estore.core.command.CancelProductReservationCommand;
import com.npdev.estore.core.event.ProductReservationCanceledEvent;
import com.npdev.estore.core.event.ProductReservedEvent;
import com.npdev.estore.product_service.command.CreateProductCommand;
import com.npdev.estore.product_service.core.event.ProductCreatedEvent;
import com.npdev.estore.core.command.ReserveProductCommand;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Slf4j
@Aggregate(snapshotTriggerDefinition = "productSnapshotTriggerDefinition")
@NoArgsConstructor // required by Axon framework
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {
        // when the CreateProductCommand is dispatched, this constructor will be used to create an aggregate
        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price cannot be less or equal than zero.");
        }

        if (StringUtils.isBlank(createProductCommand.getTitle())) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        // copy properties with the same name from source to dest object
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
        // dispatch the event that will trigger the EventSourcingHandler
        AggregateLifecycle.apply(productCreatedEvent);

//        if (true) {
//            log.info("ProductAggregate::Throwing an exception");
//            throw new RuntimeException("Runtime exception");
//        }
    }

    @CommandHandler
    public void handle(ReserveProductCommand reserveProductCommand) {
        log.info("Handling ReserveProductCommand {}...", reserveProductCommand);
        // axon loads the aggregate object for us, no need to query it,
        // it will already contain the latest state
        if (quantity < reserveProductCommand.getQuantity()) {
            throw new IllegalArgumentException("Insufficient number of items in stock.");
        }

        ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
                .orderId(reserveProductCommand.getOrderId())
                .productId(reserveProductCommand.getProductId())
                .userId(reserveProductCommand.getUserId())
                .quantity(reserveProductCommand.getQuantity())
                .build();

        AggregateLifecycle.apply(productReservedEvent);
    }

    @CommandHandler
    public void handle(CancelProductReservationCommand cancelProductReservationCommand) {
        log.info("Handling CancelProductReservationCommand {}...", cancelProductReservationCommand);
        ProductReservationCanceledEvent productReservationCanceledEvent = ProductReservationCanceledEvent.builder()
                .orderId(cancelProductReservationCommand.getOrderId())
                .productId(cancelProductReservationCommand.getProductId())
                .userId(cancelProductReservationCommand.getUserId())
                .quantity(cancelProductReservationCommand.getQuantity())
                .build();
        AggregateLifecycle.apply(productReservationCanceledEvent);
    }


    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        // initialize the latest state of the aggregate with the new event
        // when the exception happens, an event sourcing handler is not called
        // Axon does not immediately trigger this handler, but it stages it in case some error pops out
        productId = productCreatedEvent.getProductId();
        title = productCreatedEvent.getTitle();
        price = productCreatedEvent.getPrice();
        quantity = productCreatedEvent.getQuantity();
        // no business logic, just update the aggregate state
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent) {
        quantity -= productReservedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReservationCanceledEvent productReservationCanceledEvent) {
        quantity += productReservationCanceledEvent.getQuantity();
    }
}
